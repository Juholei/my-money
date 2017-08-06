(ns my-money.routes.events
  (:require [my-money.db.core :as db]
            [compojure.core :refer [defroutes GET]]
            [ring.util.http-response :as response]))

(defroutes events-routes
  (GET "/events" []
    (fn [{:keys [session]}]
      (let [username (:identity session)
            user-id (:id (db/get-user-by-username {:username username}))
            events (db/get-events {:user-id user-id})]
        (response/ok events))))
  (GET "/events/recurring/expenses" []
    (fn [{:keys [session]}]
      (let [username (:identity session)
            user-id (:id (db/get-user-by-username {:username username}))]
        (response/ok (db/get-recurring-expenses {:user-id user-id}))))))
