(ns nubank-authorizer.domain.account_test
  (:require [clojure.test :refer :all]
            [nubank-authorizer.domain.account :as acc]
            [midje.sweet :refer :all]))


(facts "about `create-account`"
       (fact "it creates a new account with activeCardtrue and limit of 100"
             (acc/createAccount "{\"account\": {\"active-card\": true, \"available-limit\": 100}}") => {:account {:activateCard true :availableLimit 100} :violations []}))