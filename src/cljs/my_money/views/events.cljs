(ns my-money.views.events
  (:require [reagent.core :as r]
            [reagent.session :as session]
            [my-money.app.controller.events :as ec]
            [my-money.app.controller.navigation :as nc]
            [my-money.calculations :as calc]
            [my-money.components.charts :as charts]
            [my-money.components.recurring-expenses :as re]
            [my-money.components.tab-bar :refer [tab-bar tab-panel]]
            [my-money.event-filters :as filters]
            [my-money.components.common :as c]
            [my-money.utils :as utils]))

(def events-on-page 50)

(defn- events-for-time-period [events period]
  (if (= period "All-time")
    events
    (let [month-filter (filters/month-filter period)]
      (filter month-filter events))))

(defn balance-info [month events savings-recipients]
  (when-let [filtered-events (events-for-time-period events month)]
    [:div.row
     [:h1.col-md-3 (str "Balance " (calc/balance filtered-events) "€")]
     [:h1.col-md-3 (str "Expenses " (-> (calc/expenses filtered-events savings-recipients)
                                        (.toFixed 2)) "€")]
     [:h1.col-md-3 (str "Income " (calc/income filtered-events) "€")]
     [:h1.col-md-3 (str "Savings " (calc/savings filtered-events savings-recipients) "€")]]))

(defn month-filter [e! events]
  [:form
   [:select {:on-change #(e! (ec/->SelectMonth (-> % .-target .-value)))}
    [:option "All-time"]
    (for [month (filters/months events)]
      ^{:key month}
      [:option month])]])

(defn event-type-selector [e! active-value]
  (r/with-let [select-type! #(e! (ec/->SelectType %))]
    [tab-bar active-value select-type!]))

(defn bank-event-table [e! events selected-events]
  [:div.table-responsive
   [:table.table.table-striped
    [:thead
     [:tr
      [:th]
      [:th "Date"]
      [:th "Amount"]
      [:th "Recipient"]]]
    [:tbody (for [event events]
              ^{:key (:id event)}
              [:tr
               [:td [:input {:type :checkbox
                             :checked (contains? selected-events event)
                             :on-change #(e! (ec/->SelectEvent event (-> % .-target .-checked)))}]]
               [:td (utils/date->pretty-string (:transaction_date event))]
               [:td (utils/amount->pretty-string (:amount event))]
               [:td (str (:recipient event))]])]]])


(defn events->pages [events number-of-events-per-page]
  (partition number-of-events-per-page number-of-events-per-page nil events))


(defn selected-events-info [e! events]
  [:div.alert.alert-info
   "Sum of selected events: " (calc/balance events) "€"
   " "
   [:a {:href "#" :on-click #(do (.preventDefault %) (e! (ec/->ClearSelectedEvents)))}
    "Clear selection"]])

(defn events-page [e! _]
  (e! (ec/->RetrieveEvents))
  (e! (ec/->RetrieveRecurringExpenses))
  (fn [e! {:keys [events filters recurring-expenses starting-amount
                  recipients event-page show-all-events?
                  selected-events]}]
    (when (session/get :identity)
      (let [filtered-events (filter (filters/combined-filter filters) events)
            paged-events (events->pages filtered-events events-on-page)
            events-to-display (if show-all-events?
                                filtered-events
                                (nth paged-events event-page nil))]
        [:div.container
         [month-filter e! events]
         [charts/chart (events-for-time-period events (:month filters))
          starting-amount events]
         [balance-info (:month filters) events recipients]
         [:div.row
          [:div.col-md-8
           [:h1 "Events"]
           [event-type-selector e! (:type filters)]
           [:div.row.justify-content-center.align-items-baseline
            [c/labeled-checkbox "Show all" show-all-events? #(e! (nc/->SetShowAllEvents %))]
            (when (not show-all-events?)
              [c/paginator event-page (count paged-events) #(e! (nc/->SelectEventPage %))])]
           [tab-panel {:id (:type filters)}
            [bank-event-table e! events-to-display selected-events]]]
          [:div.col-md-4
           [:h1 "Recurring expenses"]
           [re/recurring-expense-info recurring-expenses]]]
         (when (seq selected-events)
           [c/sticky-bottom-container
            [selected-events-info e! selected-events]])]))))
