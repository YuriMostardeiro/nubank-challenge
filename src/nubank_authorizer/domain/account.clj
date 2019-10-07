(ns nubank-authorizer.domain.account
  (:use [clojure.tools.logging :refer :all])
  (:require [cheshire.core :refer :all]))

; "Put the account information's on memory for use during all transactions"
(def accountSchema

  (atom {:account {:activateCard "false"
                   :availableLimit nil}
         :violations []}))


(defn createAccount [account]
  (info "Creating account" account)
  (swap! accountSchema assoc-in [:account :activateCard] (get-in (parse-string account true) [:account :active-card]))
  (swap! accountSchema assoc-in [:account :availableLimit] (get-in (parse-string account true) [:account :available-limit])))

(defn addAccountViolation [violation]
  (warn "Adding violation" violation)
  (swap! accountSchema assoc-in [:violations] [violation])
  )

(defn availableLimitCalculate [amount]
  (let [calculatedLimit (- (get-in @accountSchema [:account :availableLimit]) amount)]
    (if (> calculatedLimit 0)
      (swap! accountSchema assoc-in [:account :availableLimit] calculatedLimit)
      (addAccountViolation "insufficient-limit"))))

(defn verifyAccount [account]
  (let [limit (get-in @accountSchema [:account :availableLimit])]
    (if (nil? limit)
      (createAccount  account)
      (addAccountViolation "account-already-initialized")))
  (println (generate-string @accountSchema)))


