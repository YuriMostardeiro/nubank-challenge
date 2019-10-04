(ns nubank-authorizer.domain.account
    (:require [schema.core :as s]
      [clojure.set :refer [rename-keys]]
      [nubank-authorizer.main.routes :as routes]
      ;[restful-crud.string-util :as str]
      )
    )


(defn canonicalize-account-req [user-req]
       ; (-> (update user-req :password hashers/derive)
       ; (rename-keys {:password :password_hash}))
      (println (str "canonicalize" user-req))
      )

(defn valid-username? [name]
      (= 1 1))

(s/defschema AccountRequestSchema
             {:activeCard (s/constrained s/Str valid-username?)
              :availableLimit (s/constrained s/Str valid-username?)})

(def account-entity-route
  (routes/resource {
                     :name "account"
                    ;:leave #(dissoc % :password_hash)
                     :req-schema AccountRequestSchema
                     :enter canonicalize-account-req
                    })
  )


(comment "
(defprotocol Account
             (createAccount [activeCard availableLimit]))


(defn id->created [id]
      (create-account (str "                                ;/accounts/
         " id) {:id id}))

         (defn create-account [id activeCard availableLimit]
               (->AccountImplementation activeCard availableLimit))


         (defrecord AccountImplementation [id activeCard availableLimit])


         (def user-routes
           [(POST "                                         ;/accounts
         " []
                  :body [create-user-req UserRequestSchema]
                  (create-account create-user-req 1 1))])
          ")