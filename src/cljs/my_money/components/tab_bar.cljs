(ns my-money.components.tab-bar
  (:require [clojure.string :as string]
            [my-money.app.controller.events :as ec]))

(def tab-styles ["cursor-pointer"
                 "border-solid"
                 "border-2"
                 "inline-block"
                 "py-1.5"
                 "px-3"
                 "hover:text-white"
                 "text-white"
                 "bg-button-primary"
                 "border-transparent"
                 "hover:bg-button-primary-hover"
                 "active:bg-button-primary-active"
                 "focus:bg-button-primary-hover"
                 "first:rounded-l"
                 "last:rounded-r"])

(def active-tab-classes ["bg-button-primary-active" "text-white"])

(defn tab [e! active-value value type]
  [:button
   {:role "tab"
    :aria-selected (= value active-value)
    :class (into tab-styles
                 (when (= value active-value)
                   active-tab-classes))
    :id value
    :name type
    :on-click #(e! (ec/->SelectType value))}
   (string/capitalize value)])

(defn tab-bar [e! active-value]
  ;; TODO: Make this generic
  [:div {:role "tablist"}
   [tab e! active-value "all" "type"]
   [tab e! active-value "expenses" "type"]
   [tab e! active-value "incomes" "type"]])