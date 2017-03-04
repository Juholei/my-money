(ns my-money.routes.events
  (:require [my-money.db.core :as db]
            [compojure.core :refer [defroutes GET]]
            [ring.util.http-response :as response]))

(defroutes events-routes
  (GET "/events" []
    (fn [req]
      (let [username (get-in req [:params :user])
            user-id (:id (db/get-user-by-username {:username username}))
            events (db/get-events {:user-id user-id})]
        (-> (response/ok events))))))
