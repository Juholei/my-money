(ns my-money.components.common
  (:require [reagent.core :as r]
            [reagent.session :as session]))

(defn- close-modal []
  (session/remove! :modal))

(defn modal [header body footer]
  [:div
   [:div.modal-dialog
    [:div.modal-content
     [:div.modal-header [:span.modal-title.h5 header]
                        [:button.close {:type "button"
                                        :on-click close-modal}
                                       "×"]]
     [:div.modal-body body]
     [:div.modal-footer footer]]]
   [:div.modal-backdrop.fade.in {:on-click close-modal}]])

(defn alert [message close-fn]
  [:div.alert.alert-success.alert-dismissible
   [:button.close {:type "button"
                   :on-click #(close-fn)}
    "×"]
   message])
