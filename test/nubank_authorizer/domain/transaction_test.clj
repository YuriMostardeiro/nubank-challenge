(ns nubank-authorizer.domain.transaction-test
  (:require [clojure.test :refer :all]
            [nubank-authorizer.domain.transaction :as transaction]
            [midje.sweet :refer :all]))

(def accountActive {:account {:activateCard true :availableLimit 100} :violations []})
(def accountNotInitialized {:account {:activateCard false :availableLimit nil} :violations []})
(def accountCardInactive {:account {:activateCard false :availableLimit 100} :violations []})

(def transactionTestBurger {:transaction {:merchant "testBurger"
                   :amount            20
                   :time              "2019-10-06T15:00:00.000Z"
                   :transactionCount  1}})

(def transactionTestBurgerPlus1Minutes {:transaction {:merchant "testBurger"
                             :amount              20
                             :time                "2019-10-06T15:01:00.000Z"
                             :transactionCount    1} })

(def transactionTestPizza {:transaction {:merchant "testPizza"
                     :amount 20
                     :time "2019-10-06T15:00:00.000Z"
                     :transactionCount 1} })

(def transactionTestPizzaWithAmountOf50 {:transaction {:merchant "testPizza"
                           :amount 50
                           :time "2019-10-06T15:00:00.000Z"
                           :transactionCount 1} })

(def transactionThree {:transaction {:merchant "testBarbecue"
                     :amount 50
                     :time "2019-10-06T16:10:00.000Z"
                     :transactionCount 1} })

(facts "about `Account initialized`"
  (fact "when it is"
       (transaction/isAccountInitialized accountActive) => true )

  (fact "when it's not"
        (transaction/isAccountInitialized accountNotInitialized) => false ))

(facts "about `Account card active`"
       (fact "when it is"
             (transaction/isAccountCardActive accountActive) => true)

       (fact "when it's not"
             (transaction/isAccountCardActive accountCardInactive) => false))

(facts "about `Doubled Transaction`"
       (fact "when it is doubled with the same time"
             (transaction/isSingleTransaction transactionTestBurger transactionTestBurger) => false)

       (fact "when it is doubled because it was within 2 minutes of a similar transaction from the same merchant"
             (transaction/isSingleTransaction transactionTestBurger transactionTestBurgerPlus1Minutes) => false)

       (fact "when it is not doubled because it was from a different merchant"
             (transaction/isSingleTransaction transactionTestBurger transactionTestPizza) => true)

       (fact "when it is not doubled because it is from the same merchant but with a different amount"
             (transaction/isSingleTransaction transactionTestPizza transactionTestPizzaWithAmountOf50) => true)
       )
