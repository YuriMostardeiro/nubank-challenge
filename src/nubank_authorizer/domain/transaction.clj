(ns nubank-authorizer.domain.transaction
  (:require [nubank-authorizer.domain.account :as acc]
            [cheshire.core :refer :all]
            [clj-time.core :as dt]
            [clj-time.format :as f]))

(defn isAccountInitialized []
  (let [limit (get-in @acc/accountSchema [:account :availableLimit])]
    (if (nil? limit)
      (acc/accountViolation "account-not-initialized"))))

(defn isAccountLimitSufficient [amount]
  (acc/availableLimitCalculate amount))

(defn isAccountCardActive []
  (let [active (get-in @acc/accountSchema [:account :activateCard])]
    (if (not active)
      (acc/accountViolation "card-not-active"))))



(defn isOnTransactionInterval []
  ;- Não deve haver mais de 3 transações em um intervalo de 2 minutos: 'alta frequência-pequeno-intervalo'
  (println "alta frequência-pequeno-intervalo"))

(defn isSingleTransaction []
  ;- Não deve haver mais de uma transação semelhante (mesma quantidade e comerciante) em um intervalo de 2 minutos:   'transação dobrada'
  (println "transação dobrada"))

(defn doTransaction [transaction]
  (isAccountLimitSufficient (get-in (parse-string transaction true) [:transaction :amount])))


(def custom-formatter (f/formatter "yyyy-MM-dd mm:ss:SSS"))

(defn transactionRules [transaction]
  (swap! acc/accountSchema assoc-in [:violations] "")

  (isAccountInitialized)
  (isAccountCardActive)


  ;(println "data")
  ;(println (f/parse custom-formatter "2019-02-13T11:00:00.000Z"))
  ;(generate-string @accountSchema)
  ;(println (get-in (parse-string transaction true) [:transaction :time]))
  ;(t/in-minutes (t/interval (t/date-time 1986 10 2) (t/date-time 1986 10 14)))
  ;(println )

  (let [violations (get-in @acc/accountSchema [:violations])]
    (if (clojure.string/blank? violations)
        (doTransaction transaction)))

  (println (generate-string @acc/accountSchema)))
