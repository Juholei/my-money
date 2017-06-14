(ns my-money.components.common
  (:require [reagent.core :as r]
            [reagent.session :as session]))

(defn modal [header body footer]
  [:div
   [:div.modal-dialog
    [:div.modal-content
     [:div.modal-header [:span.modal-title.h5 header]
                        [:button.close {:type "button"
                                        :on-click #(session/remove! :modal)}
                                       "×"]]
     [:div.modal-body body]
     [:div.modal-footer footer]]]
   [:div.modal-backdrop.fade.in]])
