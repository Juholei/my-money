(ns my-money.components.common
  (:require [reagent.core :as r]))

(defn modal [header body footer]
  [:div
   [:div.modal-dialog
    [:div.modal-content
     [:div.modal-header header]
     [:div.modal-body body]
     [:div.modal-footer footer]]]
   [:div.modal-backdrop.fade.in]])
