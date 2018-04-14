(ns my-money.routes.upload
  (:require [my-money.db.core :as db]
            [my-money.op-csv :refer :all]
            [clj-time.jdbc]
            [compojure.core :refer [defroutes POST]]
            [ring.util.http-response :as response]))

(defn- save-events [user-id events]
  (loop [events events
         total-rows 0]
    (if-let [event (first events)]
      (let [inserted-rows (db/create-event!
                           {:transaction-id (:Arkistointitunnus event)
                            :user-id user-id
                            :transaction-date (date-string->date (:Kirjauspäivä event))
                            :amount (Integer/parseInt (clojure.string/replace (:MääräEUROA event) "," ""))
                            :recipient (:Saaja/Maksaja event)
                            :type (:Laji event)})]
        (recur (rest events) (+ total-rows inserted-rows)))
      (str total-rows))))

(defroutes upload-routes
  (POST "/upload" [file :as req]
    (let [user-id (:id (db/get-user-by-username {:username (:identity req)}))
          data (-> (:tempfile file)
                   (slurp :encoding "ISO-8859-1")
                   (read-csv)
                   (remove-columns-by-name ["Arvopäivä"])
                   (csv-vec->map))]
      (-> (response/ok (save-events user-id data))))))
