(ns nubank-authorizer.domain.transaction
  (:use [clojure.tools.logging :refer :all])
  (:require [nubank-authorizer.domain.account :as acc]
            [nubank-authorizer.util.transformUtil :as tru]
            [cheshire.core :refer :all]
            [clj-time.core :as t]
            [clj-time.format :as f]))

(def custom-formatter (f/formatter "yyyy-MM-dd HH:mm:ss.SSS "))

(def transactionSchema
  (atom {:transaction {:merchant         "false"
                       :amount           nil
                       :time             nil
                       :transactionCount 1}}))

(defn isAccountInitialized [account]
  (boolean (get-in account [:account :availableLimit])))

(defn updateTransactionSchema [transaction incrementCount]
  "updates the information  of transaction for the next one comparison"
  (swap! transactionSchema assoc-in [:transaction :merchant] (get-in transaction [:transaction :merchant]))
  (swap! transactionSchema assoc-in [:transaction :amount] (get-in transaction [:transaction :amount]))
  (swap! transactionSchema assoc-in [:transaction :time] (get-in transaction [:transaction :time]))

  (if-not (nil? incrementCount)
    (swap! transactionSchema update-in [:transaction :transactionCount] + incrementCount)
    (swap! transactionSchema assoc-in [:transaction :transactionCount] 1)))

(defn isAccountLimitSufficient [amount]
  "Return false if insufficient amount for the transaction "
  (acc/availableLimitCalculate amount))

(defn isAccountCardActive [account]
  "Return false if account card is inactive"
  (get-in account [:account :activateCard]))

(defn transactionViolation [transaction message]
  (acc/addAccountViolation message)
  (updateTransactionSchema transaction nil)
  )

(defn intervalVerify [transaction time]
  "verify if is an interval of 2 minutes"
  (let [interval (t/in-millis (t/interval (f/parse custom-formatter (tru/dateTimeFormatter time)) (f/parse custom-formatter (tru/dateTimeFormatter (get-in transaction [:transaction :time])))))]
    (<= interval 120000)))

(defn isOnTransactionInterval [previewsTransaction currentTransaction]
  "Returns false in case 4 or more transactions in a given interval"
  (info "isOnTransactionInterval" currentTransaction)
  (if (> (get-in previewsTransaction [:transaction :transactionCount]) 3)
    (not (intervalVerify currentTransaction (get-in previewsTransaction [:transaction :time])))
    true))

(defn isSingleTransaction [previewsTransaction currentTransaction]
  "Returns false in case the same transaction is called in a given interval"
  (if (= (get-in previewsTransaction [:transaction :merchant]) (get-in currentTransaction [:transaction :merchant]))
    (if (= (get-in previewsTransaction [:transaction :amount]) (get-in currentTransaction [:transaction :amount]))
      (not (intervalVerify currentTransaction (get-in previewsTransaction [:transaction :time])))
      true)
    true))

(defn doTransaction [transaction]
  (info "Do transaction")
  (isAccountLimitSufficient (get-in transaction [:transaction :amount])))

(defn transactionRules [transaction account]
  "Execute all business rules for transactions"
  (info "Transaction rules")
  (swap! acc/accountSchema assoc-in [:violations] [])

  (if-not (isAccountInitialized account)
    (acc/addAccountViolation "account-not-initialized"))

  (if-not (isAccountCardActive account)
    (acc/addAccountViolation "card-not-active"))

  (let [currentTransaction (parse-string transaction true)]
    (info "preview transaction" @transactionSchema)
    (info "current transaction" currentTransaction)
    (if-not (isSingleTransaction @transactionSchema currentTransaction)
      (transactionViolation @transactionSchema "doubled-transaction"))

    (if-not (isOnTransactionInterval @transactionSchema currentTransaction)
      (transactionViolation @transactionSchema "high-frequency-small-interval"))

    ;update the counter for high frequency tests
    (updateTransactionSchema currentTransaction 1)

    ; if no violations, check the account amount for transaction
    (let [violations (get-in @acc/accountSchema [:violations])]
      (if (empty? violations)
        (doTransaction currentTransaction))))

  (println (generate-string @acc/accountSchema)))
