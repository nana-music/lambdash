(ns lambdash.dblogic
  (:use     [clj-time.local :only (local-now)])
  (:require [monger.core :as mg]
            [clj-time.core :as clj-time]
            [taoensso.carmine :as car]
            [lambdash.settings :as settings]
            [monger.collection :as collection]
            [monger.operators :as op])
  (:import com.mongodb.ReadPreference))

;; =================
;; Common Use Const
;; =================

(def now (local-now))
(def previous-months (clj-time/minus now (clj-time/months 1)))

;;
;; Redis Server Pipeline Macro
;; -----------------------------

(defmacro wcar! [& body]
  `(car/wcar settings/redis ~@body))

;;;;;;;;;

(def db-init-message
  "
  --------------------------
  DB Initalization
  --------------------------
  ")

(defn db-connection []
  (mg/connect! settings/mongodb))

(defn use-slave-db!
  [& args]
  (mg/set-db!
    (doto (apply mg/get-db args)
          (.setReadOnly true)
          (.setReadPreference
            (ReadPreference/secondary)))))


(defn initialize []
  (println db-init-message)
  (db-connection)
  (use-slave-db! settings/database)
  (mg/authenticate
   (mg/get-db settings/database)
   (settings/auth :username)
   (settings/auth :password)))

(defn initialize-keys []
  "Initialization Redis Keys

   example, Delete Keys."
  (println "[Running...] Delete Previous Data.")
  (wcar! (car/del "foobar-month")))

(def show
  [

   ^{:doc
     "Show Data

     def Show is managed by vector and maps.
     The maps in vector elements is used.
     
     ------------------------------------------------------------------------
     @discription: When show page or write csv files, used by item name.
     @logic: function objects. When show data, used result eval function.
     ------------------------------------------------------------------------
     "
     }
   
   {:discription "Foobar Month"
    :logic
    #(wcar! (car/get "foobar-month"))}
   
   ])

(def tasks
  [
   ^{:doc "
     DataBase Tasks

     def Task is managed by vector and maps. The maps in vector elements is used:
     ------------------------------------------------------------------------
     @name: set Redis Key name.
     @kind: Now param, \"one\" or \"run-only\".
     @discroption: When show page or write csv files, used by item name.
     @group: Not Used (feature)
     @logic: function objects. @name key sets function result. 
     ------------------------------------------------------------------------
    "}

   {:name "sum-foobar"
    :kind "one"
    :discription "Count Foobar"
    :group "Not Used (feature version)"
    :logic
    #(collection/count "foobar")}])
