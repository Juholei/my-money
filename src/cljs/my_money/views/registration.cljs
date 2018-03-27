(ns my-money.views.registration
  (:require [my-money.components.common :as c]
            [my-money.app.controller.authentication :as ac]
            [reagent.core :as r]))

(defn- buttons [e! {:keys [username password]} close-fn]
  [:div
   [:input.btn.btn-primary {:type "submit"
                             :form "registration"
                             :value "Register"
                             :on-click #(do (.preventDefault %)
                                            (e! (ac/->Register username password)))}]
   [:button.btn.btn-danger {:on-click #(close-fn)}
                           "Cancel"]])

(defn- fields [data]
  [:form {:id "registration"}
   [:strong "✱ required field"]
   [c/text-input "Username" :username "Enter a username" data]
   [c/password-input "Password" :password "Enter a password" data]
   [c/password-input "Confirm your password" :pass-confirm "Type your password again" data]])

(defn registration-form [e! close-fn]
  (let [fields-data (r/atom {})]
    (fn []
      [c/modal "Register a new account"
               [fields fields-data]
               [buttons e! @fields-data close-fn]
               close-fn])))

(defn registration-button []
  [:a.nav-link.active {:href "#/register"} "Register"])
