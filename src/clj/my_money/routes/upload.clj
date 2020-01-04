(ns my-money.routes.upload
  (:require [clojure.string :as str]
            [my-money.db.core :as db]
            [my-money.op-csv :refer :all]
            [clj-time.jdbc]
            [compojure.core :refer [defroutes POST]]
            [ring.util.http-response :as response]))

(defn ->event [user-id raw-data]
  {:transaction-id (:Arkistointitunnus raw-data)
   :user-id user-id
   :transaction-date (date-string->date (:Kirjauspäivä raw-data))
   :amount (Integer/parseInt (str/replace (:MääräEUROA raw-data) "," ""))
   :recipient (:Saaja/Maksaja raw-data)
   :type (:Laji raw-data)})

(defn- save-events [user-id events]
  (loop [events events
         total-rows 0]
    (if-let [event (first events)]
      (let [inserted-rows (db/create-event!
                           (->event user-id event))]
        (recur (rest events) (+ total-rows inserted-rows)))
      (str total-rows))))

(defn csv->clj [csv]
  (-> csv
      read-csv
      csv-vec->map))

(defn ->events [x]
  (map (partial ->event 1) x))

(defroutes upload-routes
  (POST "/upload" [file :as req]
    (let [user-id (:id (db/get-user-by-username {:username (:identity req)}))
          data (-> (:tempfile file)
                   (slurp :encoding "ISO-8859-1")
                   csv->clj)]
      (response/ok (save-events user-id data)))))
