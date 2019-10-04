(defproject nubank-authorizer "0.1.0-SNAPSHOT"
  :description "FIXME: write description"
  :url "http://example.com/FIXME"
  :license {:name "EPL-2.0 OR GPL-2.0-or-later WITH Classpath-exception-2.0"
            :url "https://www.eclipse.org/legal/epl-2.0/"}
  :dependencies [[org.clojure/clojure "1.9.0"]
                           ; Web
                           [prismatic/schema "1.1.9"]
                           [metosin/compojure-api "2.0.0-alpha26"]
                           [ring/ring-jetty-adapter "1.6.3"]]

  :main ^:skip-aot nubank-authorizer.main.core
  :target-path "target/%s"
  :profiles {:uberjar {:aot :all}})
