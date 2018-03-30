(ns my-money.components.recurring-expenses
  (:require [my-money.recurring-events :as re]
            [reagent.core :as r]
            [my-money.utils :as utils]))

(defn recurring-expense-item [expense]
  (let [expense-state (r/atom {:data     expense
                               :expanded false})]
    (fn [expense]
      [:button {:class "list-group-item list-group-item-action"
                :style {:outline :none}
                :on-click #(swap! expense-state update :expanded not)}
       (str (get-in @expense-state [:data :recipient])
            " "
            (utils/amount->pretty-string (get-in @expense-state [:data :amount])))
       (when (:expanded @expense-state)
         [:div "Occurrences "
          [:ul
           (for [occurrence (get-in @expense-state [:data :events])]
             ^{:key (str (:transaction_id occurrence) (:transaction_date occurrence))}
             [:li (utils/date->pretty-string (:transaction_date occurrence))])]])])))

(defn recurring-expense-info [recurring-expenses]
  [:div.list-group
   (for [expense (re/sort-recurring-events recurring-expenses)]
     ^{:key (:recipient expense)}
     [recurring-expense-item expense])])
