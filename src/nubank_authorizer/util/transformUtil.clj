(ns nubank-authorizer.util.transformUtil)

(defn dateTimeFormatter [date]
  ;(.format (java.text.SimpleDateFormat. "MM/dd/yyyy") (.parse df (str (clojure.string/replace date #"[TZ]" " "))))
   (clojure.string/replace date #"[TZ]" " ")
  )