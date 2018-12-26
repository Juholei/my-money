(ns my-money.app.controller.authentication
  (:require [my-money.app.controller.config :as cc]
            [my-money.app.controller.common :as c]
            [my-money.app.controller.events :as ec]
            [my-money.app.controller.navigation :as nc]
            [my-money.app.state :refer [initial-state]]
            [my-money.ajax :as ajax]
            [goog.crypt.base64 :as b64]
            [reagent.session :as session]
            [tuck.core :as tuck]
            [my-money.routes :as routes]))

(defrecord Login [credentials])
(defrecord LoginSucceeded [body])
(defrecord Logout [])
(defrecord LogoutSucceeded [body])
(defrecord Register [username password])
(defrecord RegistrationSucceeded [body])

(defn- encode-auth [username password]
  (->> (str username ":" password)
       b64/encodeString
       (str "Basic ")))

(extend-protocol tuck/Event
  Login
  (process-event [{{:keys [username password]} :credentials} app]
    (tuck/fx (nc/set-in-progress app true)
             {:tuck.effect/type ::ajax/post
              :url              "/login"
              :on-success       ->LoginSucceeded
              :on-error         c/->ErrorHandler
              :headers          {"Authorization" (encode-auth username password)}}))

  LoginSucceeded
  (process-event [{{username :username} :body} app]
    (tuck/action! (fn [e!]
                    (session/put! :identity username)
                    (e! (cc/->RetrieveConfig))
                    (e! (ec/->RetrieveEvents))
                    (e! (ec/->RetrieveRecurringExpenses))
                    (routes/navigate! :home)))
    (-> app
        (nc/set-in-progress false)
        (dissoc :modal)))

  Logout
  (process-event [_ app]
    (tuck/fx app
             {:tuck.effect/type ::ajax/post
              :url              "/logout"
              :on-success       ->LogoutSucceeded
              :on-error         c/->ErrorHandler}))

  LogoutSucceeded
  (process-event [_ app]
    (session/remove! :identity)
    initial-state)

  Register
  (process-event [credentials app]
    (tuck/fx app
             {:tuck.effect/type ::ajax/post
              :url              "/register"
              :on-success       ->RegistrationSucceeded
              :on-error         c/->ErrorHandler
              :params           (into {} credentials)}))

  RegistrationSucceeded
  (process-event [{body :body} app]
    (tuck/action! (fn [_]
                    (session/put! :identity (:username body))
                    (routes/navigate! :home)))
    (dissoc app :modal)))
