(ns nubank-authorizer.domain.account
  (:require [cheshire.core :refer :all]))


(def accountSchema
  (atom {:account {:activateCard "false"
                   :availableLimit nil}
         :violations []}))


(defn createAccount [account]
  (swap! accountSchema assoc-in [:account :activateCard] (get-in (parse-string account true) [:account :active-card]))
  (swap! accountSchema assoc-in [:account :availableLimit] (get-in (parse-string account true) [:account :available-limit])))

(defn accountViolation [violation]
  (swap! accountSchema assoc-in [:violations] violation))

(defn availableLimitCalculate [amount]
  (let [calculatedLimit (- (get-in @accountSchema [:account :availableLimit]) amount)]
    (if (> calculatedLimit 0)
      (swap! accountSchema assoc-in [:account :availableLimit] calculatedLimit)
      (accountViolation "insufficient-limit")
      )))

(defn verifyAccount [account]
  (let [limit (get-in @accountSchema [:account :availableLimit])]
  (if (nil? limit)
    (createAccount  account)
    (accountViolation "account-already-initialized")))
  (println (generate-string @accountSchema)))


