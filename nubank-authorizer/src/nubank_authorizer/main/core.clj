(ns nubank-authorizer.main.core
    (:require [ring.adapter.jetty :refer [run-jetty]]
      [compojure.api.sweet :refer [api routes]]
      [nubank-authorizer.domain.account :refer [account-entity-route]]
      )
    (:gen-class))

(def swagger-config
  {:ui "/"
   :spec "/swagger.json"
   :options {:ui {:validatorUrl nil}
             :data {:info {:version "1.0.0", :title "Nubank authorizer challenge"}}}})

(def app (api {:swagger swagger-config} (apply routes account-entity-route)))

(defn -main
      [& args]
      (println "Running webserver at http:/127.0.0.1:3001" )
      (run-jetty app {:port 3001}))

