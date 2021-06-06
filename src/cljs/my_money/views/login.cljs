(ns my-money.views.login
  (:require [my-money.components.common :as c]
            [my-money.components.button :refer [button]]
            [my-money.app.controller.authentication :as ac]
            [reagent.core :as r]))


(defn login! [e! data event-handler]
  (.preventDefault event-handler)
  (e! (ac/->Login data)))

(defn- buttons [e! data close-fn]
  [:div
   [button {:type :primary
            :form "login"
            :on-click  (r/partial login! e! @data)}
    "Login"]
 [button {:type :danger
          :on-click close-fn}
  "Cancel"]])

(defn- fields [data]
  [:form {:id "login"}
   [c/text-input "Username" :username "Enter your username" data false]
   [c/password-input "Password" :password "Enter your password" data false]])

(defn login-form [e! close-fn]
  (r/with-let [data (r/atom {})]
    [c/modal "Login" [fields data] [buttons e! data close-fn] close-fn]))
