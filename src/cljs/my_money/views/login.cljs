(ns my-money.views.login
  (:require [my-money.components.common :as c]
            [my-money.components.button :refer [button]]
            [my-money.app.controller.authentication :as ac]
            [reagent.core :as r]))

(defn- buttons [e! data close-fn]
  [:div
   [:input.btn.btn-primary.ml-1 {:type "submit"
                                 :form "login"
                                 :value "Login"
                                 :on-click #(do (.preventDefault %)
                                                (e! (ac/->Login @data)))}]
   [button {:type :danger
            :on-click #(close-fn)}
    "Cancel"]])

(defn- fields [data]
  [:form {:id "login"}
   [c/text-input "Username" :username "Enter your username" data false]
   [c/password-input "Password" :password "Enter your password" data false]])

(defn login-form [e! close-fn]
  (r/with-let [data (r/atom {})]
    [c/modal "Login" [fields data] [buttons e! data close-fn] close-fn]))
