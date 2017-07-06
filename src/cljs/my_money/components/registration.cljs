(ns my-money.components.registration
  (:require [reagent.core :as r]
            [my-money.components.common :as c]))

(defn- buttons [data]
  [:div
   [:button.btn.btn-primary "Register"]
   [:button.btn.btn-danger "Cancel"]])

(defn- fields [data]
  [:div
   [:strong "✱ required field"]
   [c/text-input "Username" :username "Enter a username" data]
   [c/password-input "Password" :password "Enter a password" data]
   [c/password-input "Confirm your password" :password "Type your password again" data]])

(defn registration-form []
  (let [fields-data (r/atom {})]
    (fn []
      [c/modal "Register a new account"
               [fields fields-data]
               [buttons fields-data]])))
