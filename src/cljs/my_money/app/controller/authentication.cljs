(ns my-money.app.controller.authentication
  (:require [my-money.app.controller.config :as cc]
            [my-money.app.controller.events :as ec]
            [ajax.core :as ajax]
            [goog.crypt.base64 :as b64]
            [reagent.session :as session]
            [tuck.core :as tuck]))

(defn- encode-auth [username password]
  (->> (str username ":" password)
       b64/encodeString
       (str "Basic ")))

(defn logout! []
  (ajax/POST "/logout"
             {:handler (fn []
                         (session/remove! :identity))}))

(defn- registration-handler [data]
  (session/remove! :modal)
  (session/put! :identity (:username data))
  (reset! data {}))

(defn register! [data]
  (ajax/POST "/register"
             {:params @data
              :handler #(registration-handler data)}))

(defrecord Login [credentials])
(defrecord LoginSucceeded [credentials])

(extend-protocol tuck/Event
  Login
  (process-event [{{:keys [username password] :as data} :credentials} app]
    (tuck/action! (fn [e!]
                    (ajax/POST "/login" {:headers {"Authorization" (encode-auth username password)}
                                         :handler #(e! (->LoginSucceeded data))})))
    app)

  LoginSucceeded
  (process-event [{{:keys [username password]} :credentials} app]
    (tuck/action! (fn [e!]
                    (session/remove! :modal)
                    (session/put! :identity username)
                    (cc/get-config)
                    (e! (ec/->RetrieveEvents))
                    (e! (ec/->RetrieveRecurringExpenses))))
    app))
