(ns my-money.routes.auth
  (:require [my-money.db.core :as db]
            [buddy.hashers :as hashers]
            [compojure.core :refer [defroutes GET POST]]
            [ring.util.http-response :as response]))

(defroutes auth-routes
  (POST "/register" request
    (println request)))
