(ns my-money.components.login
  (:require [my-money.components.common :as c]
            [my-money.app.controller.config :as cc]
            [my-money.app.controller.events :as ec]
            [ajax.core :as ajax]
            [goog.crypt.base64 :as b64]
            [reagent.core :as r]
            [reagent.session :as session]))

(defn encode-auth [username password]
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
  (let [username (:username @data)
        password (:password @data)]
    (ajax/POST "/login" {:headers {"Authorization" (encode-auth username password)}
                         :handler #(login-handler data)})))

(defn- buttons [data]
  [:div
   [:input.btn.btn-primary {:type "submit"
                            :form "login"
                            :value "Login"
                            :on-click #(do (.preventDefault %)
                                           (login! data))}]
   [:button.btn.btn-danger {:on-click #(c/close-modal)} "Cancel"]])

(defn- fields [data]
  [:form {:id "login"}
   [c/text-input "Username" :username "Enter your username" data false]
   [c/password-input "Password" :password "Enter your password" data false]])

(defn login-form []
  (let [data (r/atom {})]
    (fn []
      [c/modal "Login" [fields data] [buttons data]])))

(defn login-button []
 [:a.nav-link.active {:href "#"
                      :on-click #(session/put! :modal login-form)}
  "Login"])
