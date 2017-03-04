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
                 (remove-columns-by-name ["Arvopäivä" "Laji" "Arkistointitunnus"]))]
      (let [user-id (:id (db/get-user-by-username {:username username}))]
        ; (for [event data]
        ;   (println "Adding " event " to database")
        ;   (db/create-event! {:id
        ;                      :user-id
        ;                      :transaction-date
        ;                      :amount
        ;                      :recipient
        ;                      :type}))
      (-> (response/ok data))))))
