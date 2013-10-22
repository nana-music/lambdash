(ns lambdash.handler
  (:use [monger.query :only (read-preference
                             with-collection)]
        [ring.adapter.jetty :only (run-jetty)]
        [monger.conversion :only (from-db-object)]
        [clj-time.format :only (formatters unparse)])
  (:require [compojure.handler :as handler]
            [lambdash.dblogic :as db]
            [lambdash.pagelogic :as page]
            [monger.query :as q]
            ))

(def app
  (handler/site page/app-routes))

(defn -main []
  (run-jetty page/app-routes {:port 3000}))
