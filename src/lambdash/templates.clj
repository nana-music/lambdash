(ns lambdash.templates
  (:require
   [taoensso.carmine :as car]
   [lambdash.dblogic :as db])
  (:use
   [hiccup.core :only (html)]
   [hiccup.page :only (html5)]
   [lambdash.analysis :only (wcar!)]))

(defn list-name [template-maps]
  [:li {:class "subtitle"} [:b (template-maps :name)]])

(defn list-one [template-maps]
   (html (list-name template-maps)
         [:li (template-maps :logic)]))

(defn list-map [template-maps]
   (html
    (list-name template-maps)
    (map
     #(html [:li (cond (not (nil? (template-maps "key")))
                       (%1 (template-maps :key))
                       :else (str %1))
                       ])
     (template-maps :collection))))

(defn header []
  (html
   [:head
    [:title "Lambdash"]]
    [:link {:href "/public/stylesheets/style.css", :rel "stylesheet"}]))

(defn body-header [name]
  (html
    [:header
     [:a {:href "/"} [:h1 "Lambdash"]]]
    [:h2 (str "Data :: " name)]))

(defn page [name element]
  (html5
   (header)
   (body-header name)
   [:body element]))

(defn db-to-list []
  (doall (map
   (fn [task]
     (let [name (task :discription)
           use-value (task :name)]
       (list-one {:name name :logic (wcar! (car/get use-value))})))
   (filter #(not (= (%1 :kind) "run-only")) db/tasks))))

(defn db-show-to-list []
  (doall (map
          (fn [task]
            (let [name (task :discription)
                  task (task :logic)]
              (list-one {:name name :logic (task)})))
          db/show)))
