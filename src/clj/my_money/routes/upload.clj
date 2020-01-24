(ns my-money.routes.upload
  (:require [my-money.db.core :as db]
            [my-money.services.bank-csv :as bank]
            [clj-time.jdbc]
            [compojure.core :refer [defroutes POST]]
            [ring.util.http-response :as response]
            my-money.services.op-csv
            my-money.services.spankki-csv))

(defn- save-events [user-id events]
  (loop [events events
         total-rows 0]
    (if-let [event (first events)]
      (let [inserted-rows (db/create-event!
                           (bank/->event user-id event))]
        (recur (rest events) (+ total-rows inserted-rows)))
      (str total-rows))))

(defroutes upload-routes
  (POST "/upload" [file :as req]
    (let [user-id (:id (db/get-user-by-username {:username (:identity req)}))
          data (-> (:tempfile file)
                   bank/read-bank-statement)]
      (response/ok (save-events user-id data)))))
