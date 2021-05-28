(ns my-money.views.registration
  (:require [my-money.components.button :refer [button]]
            [my-money.components.common :as c]
            [my-money.app.controller.authentication :as ac]
            [reagent.core :as r]))

(defn- buttons [e! {:keys [username password]} close-fn]
  [:div
   [button {:type :primary
            :form "registration"
            :on-click (r/partial e! (ac/->Register username password))}
    "Register"]
   [button {:type :danger
            :on-click close-fn}
    "Cancel"]])

(defn- fields [data]
  [:form {:id "registration"}
   [:strong "✱ required field"]
   [c/text-input "Username" :username "Enter a username" data]
   [c/password-input "Password" :password "Enter a password" data]
   [c/password-input "Confirm your password" :pass-confirm "Type your password again" data]])

(defn registration-form [e! close-fn]
  (r/with-let [fields-data (r/atom {})]
    [c/modal "Register a new account"
     [fields fields-data]
     [buttons e! @fields-data close-fn]
     close-fn]))
