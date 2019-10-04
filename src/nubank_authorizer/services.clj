(ns nubank-authorizer.services
  (:require [clojure.string :as str]
            [nubank-authorizer.domain.account :as acc]))

(def transactionSchema
  {:transaction {:merchant "none"
                 :amount "0"
                 :time "2019-02-13T11: 00: 00.000Z"}
   :violations []})

(defn authorizeTransaction [Transaction]
  (println Transaction)
  (println acc/accountSchema)
  (println transactionSchema)
  )

(defn checkTransaction [transaction]
  (if (str/starts-with? transaction "{\"account\":")
  (acc/verifyAccount transaction) (authorizeTransaction transaction)))


