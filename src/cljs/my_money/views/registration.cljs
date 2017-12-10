(ns my-money.views.registration
  (:require [my-money.components.common :as c]
            [my-money.app.controller.authentication :as ac]
            [reagent.core :as r]
            [reagent.session :as session]))

(defn- buttons [e! {:keys [username password]}]
  [:div
   [:input.btn.btn-primary {:type "submit"
                             :form "registration"
                             :value "Register"
                             :on-click #(do (.preventDefault %)
                                            (e! (ac/->Register username password)))}]
   [:button.btn.btn-danger {:on-click #(c/close-modal)}
                           "Cancel"]])

(defn- fields [data]
  [:form {:id "registration"}
   [:strong "✱ required field"]
   [c/text-input "Username" :username "Enter a username" data]
   [c/password-input "Password" :password "Enter a password" data]
   [c/password-input "Confirm your password" :pass-confirm "Type your password again" data]])

(defn registration-form [e!]
  (let [fields-data (r/atom {})]
    (fn []
      [c/modal "Register a new account"
               [fields fields-data]
               [buttons e! @fields-data]])))

(defn registration-button []
  [:a.nav-link.active {:href "#"
                       :on-click #(session/put! :modal registration-form)}
   "Register"])
