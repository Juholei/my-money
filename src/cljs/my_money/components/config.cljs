(ns my-money.components.config
  (:require [my-money.components.common :as c]
            [ajax.core :as ajax]
            [reagent.core :as r]
            [reagent.session :as session]))

(defn- fields [data]
  [:form
   [c/number-input "Starting amount (Amount of money before first event)" :amount "Amount" data [false]]])

(defn config-saved []
  (session/remove! :modal))

(defn save-config! [config]
  (ajax/POST "/save-config"
             {:params @config
              :handler config-saved}))

(defn- buttons [data]
  [:div
   [:button.btn.btn-primary {:on-click #(save-config! data)} "Save"]
   [:button.btn.btn-danger {:on-click #(c/close-modal)} "Cancel"]])

(defn config-modal []
  (let [fields-data (r/atom {})]
    (fn []
      [c/modal "Configuration"
               [fields fields-data]
               [buttons fields-data]])))
