(ns my-money.routes.upload
  (:require [my-money.op-csv :refer :all]
            [compojure.core :refer [defroutes POST]]
            [ring.util.http-response :as response]))

(defroutes upload-routes
  (POST "/upload" [file]
    (let [data (read-csv (slurp (:tempfile file) :encoding "ISO-8859-1"))]
      (-> (response/ok (-> data
                           (remove-column-by-name "Arvopäivä")
                           (remove-column-by-name "Laji")
                           (remove-column-by-name "Arkistointitunnus")))))))
