(ns my-money.routes.events
  (:require [my-money.db.core :as db]
            [compojure.core :refer [defroutes GET context]]
            [ring.util.http-response :as response]
            [my-money.config :as config]))

(defroutes events-routes
  (context "/events" []
    (GET "/" []
      (fn [{:keys [session]}]
        (let [username (:identity session)
              user-id (:id (db/get-user-by-username {:username username}))
              events (db/get-events {:user-id user-id})]
          (response/ok events))))
    (GET "/recurring/expenses" []
      (fn [{:keys [session]}]
        (let [username (:identity session)
              user-id (:id (db/get-user-by-username {:username username}))]
          (response/ok (db/get-recurring-expenses {:user-id user-id})))))
    (GET "/updated" [user]
      (fn [{:keys [headers]}]
        (if (= (get headers "authorization") (-> config/env :secret))
          (response/ok)
          (response/forbidden))))))
