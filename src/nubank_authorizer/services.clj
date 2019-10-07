(ns nubank-authorizer.services
  (:use [clojure.tools.logging :refer :all])
  (:require [clojure.string :as str]
            [nubank-authorizer.domain.account :as acc]
            [nubank-authorizer.domain.transaction :refer :all])
  (:import (com.fasterxml.jackson.core JsonParseException)))

(defn checkTransaction [transaction]
  (try
    (info "reading line...")
    (if (str/starts-with? transaction "{\"account\":")
      (acc/verifyAccount transaction) (transactionRules transaction @acc/accountSchema))
    (catch JsonParseException e
      (error "The input string was incorrect format: " e)
      (println "The input string was incorrect format! See the log file for more information")
      (System/exit 0))
    (catch Exception e
      (error "Oops! Something went wrong! " e)
      (println "Oops! Something went wrong! See the log file for more information")
      (System/exit 0))
    ))