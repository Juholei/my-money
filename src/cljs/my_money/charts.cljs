(ns my-money.charts
  (:require [my-money.calculations :as calc]
            [reagent.core :as r]
            [goog.object :as obj]
            [cljsjs.react-chartjs-2]))

(def bar-chart (r/adapt-react-class (obj/get js/reactChartjs2 "Bar")))

(def line-chart (r/adapt-react-class (obj/get js/reactChartjs2 "Line")))

(defn date-sum->amount [date-sum]
  (/ (:sum date-sum) 100))

(defn events->date-sums [events]
  (let [dates (set (map :transaction_date events))
        date-sum (fn [acc date]
                   (conj acc {:date date
                              :sum (calc/sum-til-date date events)}))]
    (sort-by :date < (reduce date-sum [] dates))))

(defn chart [data]
  (let [date-sums (events->date-sums data)]
    [:div
     [line-chart {:width "400px"
                  :height "100px"
                  :data {:labels (map :date date-sums)
                         :datasets [{:label "Money"
                                     :data (map date-sum->amount date-sums)
                                     :fill false}]}
                  :options {:scales {:xAxes [{:display false}]}
                            :legend {:display false}}}]]))
