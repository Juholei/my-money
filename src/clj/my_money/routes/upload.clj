(ns my-money.routes.upload
  (:require [compojure.core :refer [defroutes POST]]
            [ring.util.http-response :as response]))

(defroutes upload-routes
  (POST "/upload" [file]
       (-> (response/ok (slurp (:tempfile file)))
       (response/header "Content-Type" "text/plain; charset=utf-8"))))