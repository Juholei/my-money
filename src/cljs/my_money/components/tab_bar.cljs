(ns my-money.components.tab-bar
  (:require [clojure.string :as string]
            [my-money.app.controller.events :as ec]))

(defn tab [e! active-value value type]
  [:label.btn.btn-primary
   (when (= value active-value)
     {:class "active"})
   (string/capitalize value)
   [:input {:type "radio"
            :value value
            :id value
            :name type
            :on-click #(e! (ec/->SelectType value))}]])

(defn tab-bar [e! active-value]
  ;; TODO: Make this generic
  [:div.btn-group.btn-group-toggle
   [tab e! active-value "all" "type"]
   [tab e! active-value "expenses" "type"]
   [tab e! active-value "incomes" "type"]])