(ns nubank-authorizer.services
  (:require [clojure.string :as str]
            [nubank-authorizer.domain.account :as acc]
            [nubank-authorizer.domain.transaction :refer :all]))

; (defn authorizeTransaction [transaction account]
;     (tran/transactionRules transaction account))

(defn checkTransaction [transaction]
  (if (str/starts-with? transaction "{\"account\":")
  (acc/verifyAccount transaction) (transactionRules transaction @acc/accountSchema)))


