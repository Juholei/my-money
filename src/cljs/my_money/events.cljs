(ns my-money.events
    (:require [clojure.string :as string]
              [reagent.core :as r]
              [reagent.session :as session]
              [my-money.app.state :as state]
              [my-money.calculations :as calc]
              [my-money.charts :as charts]
              [my-money.event-filters :as filters]
              [my-money.recurring-events :as re]))

(defn- events-for-time-period [events period]
  (if (= period "All-time")
    events
    (let [month-filter (filters/month-filter period)]
      (filter month-filter events))))

(defn balance-info [month events]
  (when-let [filtered-events (events-for-time-period events month)]
    [:div.row
     [:h1.col-md-3 (str "Balance " (calc/balance filtered-events) "€")]
     [:h1.col-md-3 (str "Expenses " (-> (calc/expenses filtered-events (:recipients @state/app))
                                        (.toFixed 2)) "€")]
     [:h1.col-md-3 (str "Income " (calc/income filtered-events) "€")]
     [:h1.col-md-3 (str "Savings " (calc/savings filtered-events (:recipients @state/app)) "€")]]))

(defn labelled-radio-button [data value type]
  [:label.btn.btn-primary
   (when (= value (get-in @data [:filters :type]))
     {:class "active"})
   (string/capitalize value)
   [:input {:type "radio"
            :value value
            :id value
            :name type
            :on-click #(swap! data assoc-in [:filters :type] value)}]])

(defn month-filter [enabled-filters events]
  [:form
   [:select {:on-change #(swap! enabled-filters assoc-in [:filters :month] (-> % .-target .-value))}
    [:option "All-time"]
    (for [month (filters/months events)]
      ^{:key month}
      [:option month])]])

(defn event-type-selector [enabled-filters]
  [:div.btn-group {:data-toggle "buttons"}
   [labelled-radio-button enabled-filters "all" "type"]
   [labelled-radio-button enabled-filters "expenses" "type"]
   [labelled-radio-button enabled-filters "incomes" "type"]])

(defn amount->pretty-string [amount]
  (str (/ amount 100) "€"))

(defn date->pretty-string [date]
  (str (.getDate date)
       "."
       (inc (.getMonth date))
       "."
       (.getFullYear date)))

(defn bank-event-table [enabled-filters events]
  (let [filtered-events (filter (filters/combined-filter enabled-filters) events)]
    [:div.table-responsive
     [:table.table.table-striped
      [:thead
       [:tr
        [:th "Date"]
        [:th "Amount"]
        [:th "Recipient"]]]
      [:tbody (for [event filtered-events]
                ^{:key (:id event)}
                [:tr
                 [:td (date->pretty-string (:transaction_date event))]
                 [:td (amount->pretty-string (:amount event))]
                 [:td (str (:recipient event))]])]]]))

(defn recurring-expense-item [expense]
  (let [expense-state (r/atom {:data expense
                               :expanded false})]
    (fn [expense]
      [:button {:class "list-group-item list-group-item-action"
                :on-click #(swap! expense-state update :expanded not)}
       (str (get-in @expense-state [:data :recipient])
            " "
            (amount->pretty-string (get-in @expense-state [:data :amount])))
       (when (:expanded @expense-state)
         [:div "Occurrences "
           [:ul
            (for [occurrence (get-in @expense-state [:data :events])]
              ^{:key (str (:transaction_id occurrence) (:transaction_date occurrence))}
              [:li (date->pretty-string (:transaction_date occurrence))])]])])))

(defn recurring-expense-info [recurring-expenses]
  [:div.list-group
   (for [expense (re/sort-recurring-events recurring-expenses)]
     ^{:key (:recipient expense)}
     [recurring-expense-item expense])])

(defn events-page [{:keys [events filters recurring-expenses starting-amount] :as app}]
  (when (session/get :identity)
    [:div.container
     [month-filter state/app events]
     [charts/chart (events-for-time-period events (:month filters))
                   starting-amount]
     [balance-info (:month filters) events]
     [:div.row
      [:div.col-md-8
       [:h1 "Events"]
       [event-type-selector state/app]
       [bank-event-table filters events]]
      [:div.col-md-4
       [:h1 "Recurring expenses"]
       [recurring-expense-info recurring-expenses]]]]))
