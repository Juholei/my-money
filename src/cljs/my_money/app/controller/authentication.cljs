(ns my-money.app.controller.authentication
  (:require [my-money.app.controller.config :as cc]
            [my-money.app.controller.common :as c]
            [my-money.app.controller.events :as ec]
            [my-money.app.controller.navigation :as nc]
            [my-money.app.state :refer [initial-state]]
            [ajax.core :as ajax]
            [my-money.ajax :as my-ajax]
            [goog.crypt.base64 :as b64]
            [reagent.session :as session]
            [tuck.core :as tuck]
            [my-money.routes :as routes]))

(defrecord Login [credentials])
(defrecord LoginSucceeded [credentials])
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
  (process-event [{{:keys [username password] :as data} :credentials} app]
    (tuck/action! (fn [e!]
                    (ajax/POST "/login" {:headers {"Authorization" (encode-auth username password)}
                                         :handler #(e! (->LoginSucceeded data))
                                         :error-handler #(e! (c/->ErrorHandler %))})))
    (nc/set-in-progress app true))

  LoginSucceeded
  (process-event [{{:keys [username]} :credentials} app]
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
             {:tuck.effect/type ::my-ajax/post
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
             {:tuck.effect/type ::my-ajax/post
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
