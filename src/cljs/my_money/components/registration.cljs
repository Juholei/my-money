(ns my-money.components.registration
  (:require [my-money.components.common :as c]
            [ajax.core :as ajax]
            [reagent.core :as r]
            [reagent.session :as session]))

(defn- registration-handler [data]
  (session/remove! :modal)
  (session/put! :identity (:username data))
  (reset! data {}))

(defn register! [data]
  (ajax/POST "/register"
             {:params @data
              :handler #(registration-handler data)}))

(defn- buttons [data]
  [:div
   [:input.btn.btn-primary {:type "submit"
                             :form "registration"
                             :value "Register"
                             :on-click #(do (.preventDefault %)
                                            (register! data))}]
   [:button.btn.btn-danger {:on-click #(c/close-modal)}
                           "Cancel"]])

(defn- fields [data]
  [:form {:id "registration"}
   [:strong "✱ required field"]
   [c/text-input "Username" :username "Enter a username" data]
   [c/password-input "Password" :password "Enter a password" data]
   [c/password-input "Confirm your password" :pass-confirm "Type your password again" data]])

(defn registration-form []
  (let [fields-data (r/atom {})]
    (fn []
      [c/modal "Register a new account"
               [fields fields-data]
               [buttons fields-data]])))

(defn registration-button []
  [:a.nav-link.active {:href "#"
                       :on-click #(session/put! :modal registration-form)}
   "Register"])
