(ns nubank-authorizer.services
  (:require [clojure.string :as str]
            [nubank-authorizer.domain.account :as acc]
            [nubank-authorizer.domain.transaction :as tran]))

(defn authorizeTransaction [transaction]
    (tran/transactionRules transaction))

(defn checkTransaction [transaction]
  (if (str/starts-with? transaction "{\"account\":")
  (acc/verifyAccount transaction) (authorizeTransaction transaction)))


