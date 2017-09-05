(ns my-money.charts
  (:require [reagent.core :as r]
            [goog.object :as obj]
            [cljsjs.react-chartjs-2]))

(def bar-chart (r/adapt-react-class (obj/get js/reactChartjs2 "Bar")))

(defn event->amount [event]
  (/ (:amount event) 100))

(defn chart [data]
  [:div
   [bar-chart {:width "400px"
               :height "100px"
               :data {:labels (map :transaction_date data)
                      :datasets [{:label "data label"
                                  :data (map event->amount data)}]}
               :options {:scales {:xAxes [{:display false}]}}}]])
