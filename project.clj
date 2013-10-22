(defproject lambdash "0.1.0-SNAPSHOT"
  :description "Anything, Everything Dashboarding written by Clojure."
  :url "http://example.com/FIXME"
  :dependencies [[org.clojure/clojure "1.5.1"]
                 [org.clojure/data.csv "0.1.2"]
                 [hiccup "1.0.4"]
                 [com.novemberain/monger "1.5.0"]
                 [compojure "1.1.5"]
                 [clj-time "0.6.0"]
                 [ring/ring-jetty-adapter "1.2.0"]
                 [com.taoensso/carmine "2.3.0"]]
  :plugins [[lein-ring "0.8.5"]]
  :ring {:handler lambdash.handler/app}
  :main lambdash.analysis
  :profiles
  {:dev {:dependencies [[ring-mock "0.1.5"]]}
   :pallet {:dependencies [[com.palletops/pallet "0.8.0-RC.1"]]}}
  :aliases {"pallet" ["with-profile" "+pallet" "pallet"]})
