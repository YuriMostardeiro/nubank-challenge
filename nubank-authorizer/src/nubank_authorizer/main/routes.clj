(ns nubank-authorizer.main.routes
    (:require [compojure.api.sweet :refer [GET POST PUT DELETE routes]]
      [schema.core :as s]
      [clojure.string :as str]
      [ring.util.http-response :refer [ok not-found created]]))

(defn resource-id-path [name]
      (str "/" name "/:id"))


(def account
  (atom {:account {:activateCard "false"
                   :availableLimit "0"}}))


(defn id->created [name req-body]
      (println req-body)
      (created (str "/" name "/")
               {:account {:activateCard (+ 1 1)
                          :availableLimit (+ 2 2)}}))

(defn create-route [{:keys [name req-schema enter]}]
      (let [enter-interceptor (or enter identity)
            path (str "/" name "/create-" name)]
           (POST path http-req
                 :body [req-body req-schema]
                 (->> (enter-interceptor req-body)


                      (println @account)
                      (println (get req-body :availableLimit))
                      :req-body

                      ; (swap! account
                      ;      assoc :activateCard "true"
                      ;    :availableLimit "100")

                      (id->created name)
                      (println @account)))))
;{"account": {"active-card": true, "available-limit": 100}, "violations": []}


(comment "
(defn create-route [{:keys [name model req-schema enter]}]\n      (let [enter-interceptor (or enter identity)\n            path (str \"/\" name)]\n           (POST path http-req\n                 :body [req-body req-schema]\n                 (->> (enter-interceptor req-body)\n                      ;(db/insert! model)\n                      :id 1\n                      ;(id->created name)\n                      ))))

(defn create-route [{:keys [name req-schema enter]}]\n      (let [enter-interceptor (or enter identity)\n            path (str \"/\" name)]\n           (POST path http-req\n                 :body [req-body req-schema]\n                 (println (str \"path\" path))\n                 (println (str \"req-body\" req-body))\n                 (println (str \"enter\" enter))\n                 (println (str \"req-schema\" req-schema))\n                 )))

(defn entity->response [entity]
      (if entity
        (ok entity)
        (not-found)))

(defn get-by-id-route [{:keys [name model leave]}]
      (let [leave-interceptor (or leave identity)
            path (resource-id-path name)]
           (GET path []
                :path-params [id :- s/Int]
                (-> (model id)
                    leave-interceptor
                    entity->response))))

(defn get-all-route [{:keys [name model leave]}]
      (let [leave-interceptor (or leave identity)
            path (str "/" name)]
           (GET path []
                (->> (db/select model)
                     (map leave-interceptor)
                     ok))))

(defn update-route [{:keys [name model req-schema enter]}]
      (let [enter-interceptor (or enter identity)
            path (resource-id-path name)]
           (PUT path http-req
                :path-params [id :- s/Int]
                :body [req-body req-schema]
                (db/update! model id (enter-interceptor req-body))
                (ok))))

(defn delete-route [{:keys [name model]}]
      (let [path (resource-id-path name)]
           (DELETE path []
                   :path-params [id :- s/Int]
                   (db/delete! model :id id)
                   (ok))))
")
(defn resource [resource-config]
      (routes
        (create-route resource-config)
        ;(get-by-id-route resource-config)
        ;(get-all-route resource-config)
        ;(update-route resource-config)
        ;(delete-route resource-config)
        ))