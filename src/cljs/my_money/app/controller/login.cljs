(ns my-money.app.controller.login
  (:require [my-money.app.controller.config :as cc]
            [my-money.app.controller.events :as ec]
            [ajax.core :as ajax]
            [goog.crypt.base64 :as b64]
            [reagent.session :as session]))

(defn- encode-auth [username password]
  (->> (str username ":" password)
       b64/encodeString
       (str "Basic ")))

(defn- login-handler [data]
  (session/remove! :modal)
  (session/put! :identity (:username @data))
  (reset! data {})
  (cc/get-config)
  (ec/get-events))

(defn login! [data]
  (let [{:keys [username password]} @data]
    (ajax/POST "/login" {:headers {"Authorization" (encode-auth username password)}
                         :handler #(login-handler data)})))