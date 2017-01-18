(ns my-money.routes.upload
  (:require [my-money.op-csv :refer [read-csv]]
            [compojure.core :refer [defroutes POST]]
            [ring.util.http-response :as response]))

(defroutes upload-routes
  (POST "/upload" [file]
    (let [data (read-csv (slurp (:tempfile file)))]
      (-> (response/ok data)))))
