(ns my-money.components.login
  (:require [my-money.components.common :as c]
            [my-money.app.controller.authentication :as ac]
            [reagent.core :as r]
            [reagent.session :as session]))

(defn- buttons [data]
  [:div
   [:input.btn.btn-primary {:type "submit"
                            :form "login"
                            :value "Login"
                            :on-click #(do (.preventDefault %)
                                           (ac/login! data))}]
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
