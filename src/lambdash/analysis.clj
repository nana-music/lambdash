(ns lambdash.analysis
  (:require
   [clj-time.local :as time-local]
   [clj-time.format :as time-format]
   [lambdash.settings :as settings]
   [taoensso.carmine :as car :refer (wcar)]
   [clojure.java.io :as io]
   [clojure.data.csv :as csv]
   [lambdash.dblogic :as db]))

;;
;; Common Format Utility
;;


(def filename-format
  (time-format/formatter "yyyyMMdd"))

(def csv-title-format
  (time-format/formatter "yyyy-MM-dd hh:mm:ss"))

(def filename
  (str
   (time-format/unparse filename-format (time-local/local-now))
   ".csv"))

(def csv-title
  (str
   (time-format/unparse csv-title-format (time-local/local-now))))

(def csv-dirname "csv/")

;;
;; Redis Server Pipeline Macro
;; -----------------------------

(defmacro wcar! [& body]
  `(car/wcar settings/redis ~@body))

;;;;;;;

(def init-message
  "
  -------------------------------------------
  !!!! Lambdash !!!!
                        Everything, Anything
                        analyze your self.
  -------------------------------------------
  ")

(defn task-delete-keys [task]
  (let [key-name (task :name)]
    (wcar! (car/del key-name))))

(defn set-in-redis! [task]
  (let [use-table (task :group)
        key-name (task :name)
        kind (task :kind)
        logic (task :logic)
        message (task :discription)]
    (println (str "[now running]" message))
    (let [result (logic)]
      (wcar! (car/set key-name (logic)))
      (println (str "Result >>> " result)))))

(defn filter-kind [tasks]
  (filter #(= "one" (%1 :kind)) tasks))

(defn task-runner []
  (do
   (dorun (map #(task-delete-keys %1) (filter-kind db/tasks))) 
   (db/initialize-keys)
   (dorun
   (map set-in-redis!
        (filter #(= "one" (%1 :kind)) db/tasks)))
   (dorun (map #(do
                  (println (str "[now running]" (%1 :discription)))
                  ((%1 :logic)))
               (filter #(= "run-only" (%1 :kind)) db/tasks)))))

(defn redis-connect-check []
  (println
   (str
    "Connection redis -->" (wcar! (car/ping)))))

(defn data-vector []
  (concat (doall (map #(vector (%1 :discription)
                (wcar! (car/get (%1 :name))))
                      (filter #(not (= (%1 :kind) "run-only")) db/tasks)))
        (doall (map #(vector (%1 :discription)
                             ((%1 :logic))) db/show))))

(defn csv-writer []
  (println "[Running] Csv write...")
  (with-open [out-file (io/writer (str csv-dirname filename))]
    (csv/write-csv out-file
                   (cons [csv-title] (data-vector)))))

(defn -main []
  (println init-message)
  (redis-connect-check)
  (cond
   (= "PONG" (wcar! (car/ping)))
   (do
     (db/initialize)
     (task-runner)
     (csv-writer))))
