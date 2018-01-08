(ns my-money.app.controller.authentication
  (:require [my-money.app.controller.config :as cc]
            [my-money.app.controller.events :as ec]
            [my-money.app.controller.navigation :as nc]
            [my-money.app.state :refer [initial-state]]
            [ajax.core :as ajax]
            [goog.crypt.base64 :as b64]
            [reagent.session :as session]
            [tuck.core :as tuck]))

(defrecord Login [credentials])
(defrecord LoginSucceeded [credentials])
(defrecord Logout [])
(defrecord LogoutSucceeded [])
(defrecord Register [username password])
(defrecord RegistrationSucceeded [username])
(defrecord ErrorHandler [])

(defn- encode-auth [username password]
  (->> (str username ":" password)
       b64/encodeString
       (str "Basic ")))

(defn- logout! [e!]
  (ajax/POST "/logout"
             {:handler #(e! (->LogoutSucceeded))}))

(extend-protocol tuck/Event
  Login
  (process-event [{{:keys [username password] :as data} :credentials} app]
    (tuck/action! (fn [e!]
                    (ajax/POST "/login" {:headers {"Authorization" (encode-auth username password)}
                                         :handler #(e! (->LoginSucceeded data))
                                         :error-handler #(e! (->ErrorHandler))})))
    (nc/set-in-progress app true))

  LoginSucceeded
  (process-event [{{:keys [username password]} :credentials} app]
    (tuck/action! (fn [e!]
                    (session/put! :identity username)
                    (e! (cc/->RetrieveConfig))
                    (e! (ec/->RetrieveEvents))
                    (e! (ec/->RetrieveRecurringExpenses))))
    (-> app
        (nc/set-in-progress false)
        (dissoc :modal)))

  Logout
  (process-event [_ app]
    (tuck/action! logout!)
    app)

  LogoutSucceeded
  (process-event [_ app]
    (session/remove! :identity)
    initial-state)

  Register
  (process-event [{:keys [username] :as credentials} app]
    (tuck/action! (fn [e!]
                    (ajax/POST "/register"
                               {:params (into {} credentials)
                                :handler #(e! (->RegistrationSucceeded username))})))
    app)

  RegistrationSucceeded
  (process-event [{username :username} app]
    (tuck/action! (fn [_]
                    (session/put! :identity username)))
    (dissoc app :modal))

  ErrorHandler
  (process-event [_ app]
    (nc/set-in-progress app false)))
