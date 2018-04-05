(ns my-money.components.charts
  (:require [my-money.calculations :as calc]
            [my-money.components.common :as c]
            [reagent.core :as r]
            [goog.object :as obj]
            [cljsjs.react-chartjs-2]))

(def line-chart (r/adapt-react-class (obj/get js/ReactChartjs2 "Line")))

(defn date-sum->amount [date-sum]
  (/ (:sum date-sum) 100))

(defn events->date-sums [events-to-display starting-amount all-events]
  (let [dates (set (map :transaction_date events-to-display))
        date-sum (fn [acc date]
                   (conj acc {:date date
                              :sum (+ starting-amount (calc/sum-til-date date all-events))}))]
    (sort-by :date < (reduce date-sum [] dates))))

(defn- moneyfy-y-label [tooltip]
  (str (.-yLabel tooltip) "€"))

(defn chart [events-to-show starting-amount all-events]
  (let [collapsed? (r/atom false)]
    (fn [events-to-show starting-amount events]
      (let [date-sums (events->date-sums events-to-show starting-amount events)
            chart-options {:width 400
                           :height 100
                           :data {:labels (map :date date-sums)
                                  :datasets [{:data (map date-sum->amount date-sums)
                                              :fill false}]}
                           :options {:scales {:xAxes [{:display false}]}
                                     :legend {:display false}}}]

        [:div.container
         [c/collapsing-button collapsed?]
         [:div.row {:class (when @collapsed? "collapse")}
          [line-chart chart-options]]]))))
