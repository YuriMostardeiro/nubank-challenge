(ns nubank-authorizer.core
  (:use [clojure.string :only (join)])
  (:require
    [clojure.tools.cli :refer [parse-opts]]
    [nubank-authorizer.services :as services])
  (:gen-class))

(defn exit
  "Exits the program with a status code and message."
  [status msg]
  (println msg)
  (System/exit status))

(defn main
  "Main function.  Calling without a file object results in reading from STDIN (*in*).
  Otherwise, opens the in-file as a stream."
  ([]
   (main (java.io.BufferedReader. *in*)))
  ([in-file]
   (with-open [read in-file]
     (doseq [line (line-seq read)]
       (services/checkTransaction line)))))

(def cli-options
  [["-h" "--help"]])

(defn -main
  "Entrypoint, parses arguments, exits with any errors, provides args to main."
  [& args]
  (let [{:keys [options arguments errors summary]} (parse-opts args cli-options)]
    (cond
      (:help options) (exit 0 summary)
      errors (exit 1 (join "\n" errors))
      (empty? arguments) (main)
      :else (main (clojure.java.io/reader (first arguments))))))