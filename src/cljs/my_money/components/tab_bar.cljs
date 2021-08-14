(ns my-money.components.tab-bar
  (:require [clojure.string :as string]
            [reagent.core :as r]))

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

(defn tab [active-value value type on-select]
  [:button
   {:role "tab"
    :aria-controls value
    :aria-selected (= value active-value)
    :class (into tab-styles
                 (when (= value active-value)
                   active-tab-classes))
    :id value
    :name type
    :on-click (r/partial on-select value)}
   (string/capitalize value)])

(defn tab-bar [active-value on-select]
  ;; TODO: Make this generic
  [:div {:role "tablist"}
   [tab active-value "all" "type" on-select]
   [tab active-value "expenses" "type" on-select]
   [tab active-value "incomes" "type" on-select]])

(defn tab-panel [props content]
  [:div {:role "tabpanel" :id (:id props)}
   content])