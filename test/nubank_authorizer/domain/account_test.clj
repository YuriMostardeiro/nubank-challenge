(ns nubank-authorizer.domain.account_test
  (:require [clojure.test :refer :all]
            [nubank-authorizer.domain.account :as acc]
            [midje.sweet :refer :all]))


(facts "about `create-user`"
       (fact "it creates a new user with the given id"
             (acc/createAccount "{\"account\": {\"active-card\": true, \"available-limit\": 100}}") => {:account {:activateCard "true" :availableLimit 100} :violations []}))