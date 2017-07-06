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

(defn input [type id placeholder fields]
  [:input.form-control.input-lg {:type type
                                 :placeholder placeholder
                                 :value (id @fields)
                                 :on-change #(swap! fields assoc id (-> % .-target .-value))}])

(defn form-input [type label id placeholder fields optional?]
  [:div.form-group
   [:label label
    (if optional?
      [input type id placeholder fields]
      [:div.input-group
       [input type id placeholder fields]
       [:span.input-group-addon "✱"]])]])

(defn text-input [label id placeholder fields & [optional?]]
  (form-input :text label id placeholder fields optional?))

(defn password-input [label id placeholder fields]
  (form-input :password label id placeholder fields optional?))
