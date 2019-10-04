(ns nubank-authorizer.domain.transaction
    (:require [nubank-authorizer.util.transactionUtil :as str] [nubank-authorizer.domain.account :as acc])
    ([nubank-authorizer.domain.account :as acc]))


(defprotocol Transaction
             (createTransaction [merchant amount time]))




(defn transactionValueValid? [amount]
      (str/transactionValue acc/Account amount))