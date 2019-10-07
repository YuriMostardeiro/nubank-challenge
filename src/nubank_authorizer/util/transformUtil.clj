(ns nubank-authorizer.util.transformUtil)

(defn dateTimeFormatter [date]
   (clojure.string/replace date #"[TZ]" " ")
  )