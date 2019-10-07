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
  (swap! transactionSchema assoc-in [:transaction :merchant] (get-in transaction [:transaction :merchant]))
  (swap! transactionSchema assoc-in [:transaction :amount] (get-in transaction [:transaction :amount]))
  (swap! transactionSchema assoc-in [:transaction :time] (get-in transaction [:transaction :time]))

  (if-not (nil? incrementCount)
    (swap! transactionSchema update-in [:transaction :transactionCount] + incrementCount)
    (swap! transactionSchema assoc-in [:transaction :transactionCount] 1)))

(defn isAccountLimitSufficient [amount]
  (acc/availableLimitCalculate amount))

(defn isAccountCardActive [account]
  (get-in account [:account :activateCard]))

(defn transactionViolation [transaction message]
  (info "VIOLATION: " message transaction)
  (acc/addAccountViolation message)
  (updateTransactionSchema transaction nil)
  )

(defn intervalVerify [transaction time]
  (let [interval (t/in-millis (t/interval (f/parse custom-formatter (tru/dateTimeFormatter time)) (f/parse custom-formatter (tru/dateTimeFormatter (get-in transaction [:transaction :time])))))]
    (<= interval 120000)))

(defn isOnTransactionInterval [previewsTransaction currentTransaction]
  (info "isOnTransactionInterval")
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

;(defn intervalVerify [transaction time]
;  (let [interval (t/in-millis (t/interval (f/parse custom-formatter  (tru/dateTimeFormatter time)) (f/parse custom-formatter (tru/dateTimeFormatter (get-in (parse-string transaction true) [:transaction :time])))))]
;    (if (< interval 120000)
;      (isSingleTransaction transaction)
;      (updateTransactionSchema transaction 1))))



(defn doTransaction [transaction]
  (isAccountLimitSufficient (get-in transaction [:transaction :amount])))

(defn transactionRules [transaction account]
  (swap! acc/accountSchema assoc-in [:violations] [])

  (if-not (isAccountInitialized account)
    (acc/addAccountViolation "account-not-initialized"))

  (if-not (isAccountCardActive account)
    (acc/addAccountViolation "card-not-active"))

  ;(let [time (get-in @transactionSchema [:transaction :time])]
  ;  (if (nil? time)
  ;    (updateTransactionSchema transaction nil)
  ;    (if (intervalVerify transaction time)
  ;      ())))

  (let [currentTransaction (parse-string transaction true)]
    (info @transactionSchema currentTransaction)
    (if-not (isSingleTransaction @transactionSchema currentTransaction)
      (transactionViolation @transactionSchema "doubled-transaction"))

    (if-not (isOnTransactionInterval @transactionSchema currentTransaction)
      (transactionViolation @transactionSchema "high-frequency-small-interval")))


    ;(transactionViolation currentTransaction "doubled-transaction")
    ;(isOnTransactionInterval currentTransaction)
    (info @transactionSchema)
    (updateTransactionSchema currentTransaction 1)

    (let [violations (get-in @acc/accountSchema [:violations])]
      (if (empty? violations)
        (doTransaction currentTransaction)))


    )

  (println (generate-string @acc/accountSchema)))
