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

(defn decode-auth [encoded]
  (let [auth (second (.split encoded " "))]
    (-> (.decode (java.util.Base64/getDecoder) auth)
        (String. (java.nio.charset.Charset/forName "UTF-8"))
        (.split ":"))))

(defn authenticate [[username password]]
  (if-let [user (db/get-user-by-username {:username username})]
    (when (hashers/check password (:password user))
      username)))

(defn login! [{:keys [session headers]}]
  (if-let [user (authenticate (decode-auth (get headers "authorization")))]
    (-> {:result :ok}
        (response/ok)
        (assoc :session (assoc session :identity user)))
    (response/unauthorized {:result :unauthorized
                            :message "login failure"})))

(defn logout! []
  (-> (:result :ok)
      (response/ok)
      (assoc :session nil)))

(defroutes auth-routes
  (POST "/register" request
    (register! request))
  (POST "/login" request
    (login! request))
  (POST "/logout" request
    (logout!)))
