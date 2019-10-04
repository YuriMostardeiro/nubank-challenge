(ns nubank-authorizer.domain.account
  (:require [cheshire.core :refer :all]))


(def accountSchema
  (atom {:account {:activateCard "false"
                   :availableLimit "0"}
         :violations []}))


(defn createAccount [activateCard availableLimit]
  (swap! accountSchema assoc-in [:account :activateCard] activateCard)    ;(get account :active-card)
  (swap! accountSchema assoc-in [:account :availableLimit] availableLimit) ;(get account :available-limit)
  )

(defn accountViolation [violation]
  (println "violation")
  ;(swap! accountSchema update-in [:account :availableLimit] + violation)
  ;(swap! @accountSchema update-in [:violations] violation)

  ;(map (fn [x] (swap! accountSchema(update-in x [:violations] #(if (= "name2" %) % "not 2"))) accountSchema))
  )


(defn verifyAccount [account]
  ;(println account)
  ;(println(cheshire.core/parse-string (:body account)))
  ;(println(cheshire.core/generate-string account))
  ;(println (get-in account ["account" ":active-card"]))

  (if (boolean (resolve 'accountSchema))
    (accountViolation "account-already-initialized")
    (createAccount (get-in (parse-string account true) [:account :active-card]) (get-in (parse-string account true) [:account :available-limit])))

  (println (generate-string @accountSchema)))


