(ns nubank-authorizer.domain.transaction
  (:require [nubank-authorizer.domain.account :as acc]
            [nubank-authorizer.util.transformUtil :as tru]
            [cheshire.core :refer :all]
            [clj-time.core :as t]
            [clj-time.format :as f]))

(def custom-formatter (f/formatter "yyyy-MM-dd HH:mm:ss.SSS "))

(def transactionSchema
  (atom {:transaction {:merchant "false"
                       :amount nil
                       :time nil
                       :transactionCount 1}}))

(defn isAccountInitialized []
  (let [limit (get-in @acc/accountSchema [:account :availableLimit])]
    (if (nil? limit)
      (acc/accountViolation "account-not-initialized"))))

(defn updateTransactionSchema [transaction incrementCount]
  (swap! transactionSchema assoc-in [:transaction :merchant] (get-in (parse-string transaction true) [:transaction :merchant]))
  (swap! transactionSchema assoc-in [:transaction :amount] (get-in (parse-string transaction true) [:transaction :amount]))
  (swap! transactionSchema assoc-in [:transaction :time] (get-in (parse-string transaction true) [:transaction :time]))

  (if-not (nil? incrementCount)
    (swap! transactionSchema update-in [:transaction :transactionCount] + incrementCount)
    (swap! transactionSchema assoc-in [:transaction :transactionCount] 1)))

(defn isAccountLimitSufficient [amount]
  (acc/availableLimitCalculate amount))

(defn isAccountCardActive []
  (let [active (get-in @acc/accountSchema [:account :activateCard])]
    (if (not active)
      (acc/accountViolation "card-not-active"))))

(defn transactionViolation [transaction message]
  (acc/accountViolation message)
  (updateTransactionSchema transaction nil)
  )

(defn isOnTransactionInterval [transaction]
  (if (> (get-in  @transactionSchema [:transaction :transactionCount]) 3)
    (transactionViolation transaction "high-frequency-small-interval")
    (updateTransactionSchema transaction 1)))

(defn isSingleTransaction [transaction]
  (if (= (get-in  @transactionSchema [:transaction :merchant]) (get-in (parse-string transaction true) [:transaction :merchant]))
    (if (= (get-in  @transactionSchema [:transaction :amount]) (get-in (parse-string transaction true) [:transaction :amount]))
      (transactionViolation transaction "doubled-transaction")
      ))
  (isOnTransactionInterval transaction))

(defn intervalVerify [transaction time]
  ;(println (f/parse custom-formatter (tru/dateTimeFormatter (get-in (parse-string transaction true) [:transaction :time]))))
  ;(println (f/parse custom-formatter  (tru/dateTimeFormatter time)))

  (let [interval (t/in-millis (t/interval (f/parse custom-formatter  (tru/dateTimeFormatter time)) (f/parse custom-formatter (tru/dateTimeFormatter (get-in (parse-string transaction true) [:transaction :time])))))]
    (if (< interval 120000)
      (isSingleTransaction transaction)
      (updateTransactionSchema transaction 1)
      )))

(defn doTransaction [transaction]
  (isAccountLimitSufficient (get-in (parse-string transaction true) [:transaction :amount])))

(defn transactionRules [transaction]
  (swap! acc/accountSchema assoc-in [:violations] "")

  (isAccountInitialized)
  (isAccountCardActive)

  ;(println (tru/dateTimeFormatter (get-in (parse-string transaction true) [:transaction :time])))
  ;(println (get-in (parse-string transaction true) [:transaction :time]))


  ;(println (f/parse custom-formatter "2019-02-13T11:00:00.000Z"))
  ;(generate-string @accountSchema)

  ;(println (f/parse "2019-05-05 04:03:27.000"))
  ;(println (f/parse custom-formatter "2019-02-02 11:00:00.000"))
  ;(println (t/in-millis (t/interval (f/parse custom-formatter  (tru/dateTimeFormatter (get-in (parse-string transaction true) [:transaction :time]))) (t/date-time 2019 11 11 11 03 27 456))))
  ;(println (t/in-minutes (t/interval (t/date-time 1986 10 14 4 3 27 456) (t/date-time 1986 10 14 4 4 29 456))))

  ;(f/parse (f/formatters :basic-date-time) \"2019-02-13T11:00:00.000Z\")
  ;(println )


  ;(if (> (get-in @transaction [:transaction :transactionCount]) 0))

  ;(println (str "transactionmemory- "@transactionSchema))
  ;(println (str "memory-" (get-in @transactionSchema [:transaction :time])))
  ;(println (str "transaction-" (get-in (parse-string transaction true) [:transaction :time])))

  (let [time (get-in @transactionSchema [:transaction :time])]
    (if (nil? time)
      (updateTransactionSchema transaction nil)
      (intervalVerify transaction time)))

  (let [violations (get-in @acc/accountSchema [:violations])]
    (if (clojure.string/blank? violations)
        (doTransaction transaction)))

  (println (generate-string @acc/accountSchema)))
