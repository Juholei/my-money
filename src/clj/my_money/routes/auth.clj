(ns my-money.routes.auth
  (:require [my-money.db.core :as db]
            [buddy.hashers :as hashers]
            [compojure.core :refer [defroutes GET POST]]
            [ring.util.http-response :as response]))

(defn register! [{:keys [session params]}]
  (db/create-user! (-> params
                       (dissoc :pass-confirm)
                       (update :password hashers/encrypt)))
  (-> {:result :ok}
      (response/ok)
      (assoc :session (assoc session :identity (:username params)))))

(defn login![req]
  (println req))

(defroutes auth-routes
  (POST "/register" request
    (register! request))
  (POST "/login" request
    (login! request)))
