(ns nubank-authorizer.util.transactionUtil)

; Defines if the account have the amount value required for the transaction
(defn transactionValue [accountAmount transactionValue]
      (> accountAmount transactionValue))
