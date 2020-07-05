(ns my-money.components.charts
  (:require [my-money.calculations :as calc]
            [my-money.components.common :as c]
            [reagent.core :as r]
            ["react-chartjs-2" :refer [Line]]))

(def line-chart (r/adapt-react-class Line))

(defn date-sum->amount [date-sum]
  (/ (:sum date-sum) 100))

(defn events->date-sums [events-to-display starting-amount all-events]
  (let [dates (set (map :transaction_date events-to-display))
        date-sum (fn [acc date]
                   (conj acc {:date date
                              :sum (+ starting-amount (calc/sum-til-date date all-events))}))]
    (sort-by :date < (reduce date-sum [] dates))))

(defn- moneyfy-y-label [tooltip]
  (-> tooltip
      js->clj
      (get "yLabel")
      (str "€")))

(defn chart [events-to-show starting-amount events]
  (r/with-let [collapsed? (r/atom false)]
    (let [date-sums (events->date-sums events-to-show starting-amount events)
          chart-options {:width   400
                         :height  100
                         :data    {:labels   (map :date date-sums)
                                   :datasets [{:data (map date-sum->amount date-sums)
                                               :fill false}]}
                         :options {:scales   {:xAxes [{:display false}]}
                                   :legend   {:display false}
                                   :tooltips {:callbacks {:label moneyfy-y-label}}}}]

      [:div.container
       [c/collapsing-button collapsed?]
       [:div.row {:class (when @collapsed? "collapse")}
        [line-chart chart-options]]])))
