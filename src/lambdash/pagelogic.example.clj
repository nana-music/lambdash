(ns lambdash.pagelogic
  (:use
   [compojure.core]
   [hiccup.core :only (html)])
  (:require [lambdash.templates :as templates]
            [lambdash.settings :as settings]
            [lambdash.dblogic :as db]
            [compojure.route :as route]
            [taoensso.carmine :as car]))

(defn index []
  (templates/page
   
   "Index"

   (html [:div {:id "group-user" :class "column"}
          [:ul
           [:li {:class "link"}
            [:a {:href (str "/" prefix "/foobar")} "Link Page"]]]
          [:ul (templates/db-to-list)
           ]]
         [:div {:id "group-applauce" :class "column"}
          [:ul (templates/db-show-to-list)]])))

(defroutes app-routes
  (GET "/" [] (index))
  (route/files "/" {:root "resources"})
  (route/not-found "Not Found"))
