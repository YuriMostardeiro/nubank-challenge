(ns nubank-authorizer.domain.authorization
    (:require [nubank-authorizer.util.authorizationUtil :as str]
      [nubank-authorizer.domain.account :as acc]))


(defprotocol Authorization
             (createAuthorization [acc/Account violations]))