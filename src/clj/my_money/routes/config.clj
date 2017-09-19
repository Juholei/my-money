(ns my-money.routes.config
  (:require [my-money.db.core :as db]
            [compojure.core :refer [defroutes POST]]
            [ring.util.http-response :as response]))

(defroutes config-routes
  (POST "/save-config" []
    (fn [{:keys [session params]}]
      (let [username (:identity session)
            user-id (:id (db/get-user-by-username {:username username}))]
        (println params)
        (response/ok)))))
