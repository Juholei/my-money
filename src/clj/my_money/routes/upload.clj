(ns my-money.routes.upload
  (:require [my-money.db.core :as db]
            [my-money.op-csv :refer :all]
            [compojure.core :refer [defroutes POST]]
            [ring.util.http-response :as response]))

(defroutes upload-routes
  (POST "/upload" [file username]
    (when (not (db/get-user-by-username {:username username}))
      (db/create-user! {:username username}))

    (let [data (read-csv (slurp (:tempfile file) :encoding "ISO-8859-1"))]
      (-> (response/ok (-> data
                           (remove-column-by-name "Arvopäivä")
                           (remove-column-by-name "Laji")
                           (remove-column-by-name "Arkistointitunnus")))))))
