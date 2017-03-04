(ns my-money.routes.upload
  (:require [my-money.db.core :as db]
            [my-money.op-csv :refer :all]
            [compojure.core :refer [defroutes POST]]
            [ring.util.http-response :as response]))

(defroutes upload-routes
  (POST "/upload" [file username]
    (when-not (db/get-user-by-username {:username username})
      (db/create-user! {:username username}))

    (let [data (-> (:tempfile file)
                   (slurp :encoding "ISO-8859-1")
                   (read-csv)
                   (remove-columns-by-name ["Arvopäivä" "Laji"])
                   (csv-vec->map))]
      (let [user-id (:id (db/get-user-by-username {:username username}))]
        (loop [events data]
          (when-let [event (first events)]
            (db/create-event! {:id (:Arkistointitunnus event)
                               :user-id user-id
                               :transaction-date (:Kirjauspäivä event)
                               :amount (bigdec (clojure.string/replace (:MääräEUROA event) "," "."))
                               :recipient (:Saaja/Maksaja event)
                               :type (:Laji event)})
            (recur (rest events)))))
      (-> (response/ok data)))))

