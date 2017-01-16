(ns my-money.routes.upload
  (:require [clojure.data.csv :as csv]
            [compojure.core :refer [defroutes POST]]
            [ring.util.http-response :as response]))

(defroutes upload-routes
  (POST "/upload" [file]
       (let [data (csv/read-csv (slurp (:tempfile file)) :separator \;)]
           (-> (response/ok data)
           (response/header "Content-Type" "text/plain; charset=utf-8")))))