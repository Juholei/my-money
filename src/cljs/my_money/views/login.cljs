(ns my-money.views.login
  (:require [my-money.components.common :as c]
            [my-money.app.controller.authentication :as ac]
            [my-money.app.controller.navigation :as nc]
            [reagent.core :as r]))

(defn- buttons [e! data]
  [:div
   [:input.btn.btn-primary {:type "submit"
                            :form "login"
                            :value "Login"
                            :on-click #(do (.preventDefault %)
                                           (e! (ac/->Login @data)))}]
   [:button.btn.btn-danger {:on-click #(e! (nc/->CloseModal))} "Cancel"]])

(defn- fields [data]
  [:form {:id "login"}
   [c/text-input "Username" :username "Enter your username" data false]
   [c/password-input "Password" :password "Enter your password" data false]])

(defn login-form [e! close-fn]
  (let [data (r/atom {})]
    (fn []
      [c/modal "Login" [fields data] [buttons e! data] close-fn])))

(defn login-button []
 [:a.nav-link.active {:href "#/login"} "Login"])
