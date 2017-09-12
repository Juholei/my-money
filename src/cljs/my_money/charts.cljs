(ns my-money.charts
  (:require [my-money.calculations :as calc]
            [my-money.components.common :as c]
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

(defn- moneyfy-y-label [tooltip]
  (str (.-yLabel tooltip) "€"))

(defn chart [data]
  (let [collapsed? (r/atom false)]
    (fn [data]
      (let [date-sums (events->date-sums data)
            chart-options {:width 400
                           :height 100
                           :data {:labels (map :date date-sums)
                                  :datasets [{:data (map date-sum->amount date-sums)
                                              :fill false}]}
                           :options {:scales {:xAxes [{:display false}]}
                                     :legend {:display false}
                                     :tooltips {:callbacks {:label moneyfy-y-label}}}}]

        [:div.container
         [c/collapsing-button collapsed?]
         [:div.row {:class (when @collapsed? "collapse")}
          [line-chart chart-options]]]))))
