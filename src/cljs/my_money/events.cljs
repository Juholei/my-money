(ns my-money.events
    (:require [clojure.string :as string]
              [reagent.core :as r]
              [ajax.core :refer [GET]]
              [my-money.calculations :as calc]
              [my-money.event-filters :as filters]
              [my-money.recurring-events :as re]))

(defonce response-data (r/atom nil))
(defonce recurring-expenses (r/atom nil))

(defonce form-data (r/atom {:username ""
                            :selected-filters {:type "all"
                                               :month "All-time"}}))

(defn handle-response [response]
  (reset! response-data response))

(defn recurring-expenses-handler [response]
  (reset! recurring-expenses response))

(defn get-recurring-expenses []
 (GET "/events/recurring/expenses" {:handler recurring-expenses-handler
                                    :params {:user (:username @form-data)}}))

(defn get-events
  ([]
   (get-events (:username @form-data)))
  ([username]
   (get-recurring-expenses)
   (GET "/events" {:handler handle-response
                   :params {:user username}})))

(defn balance-info [events]
  (when events
    [:div.container
     [:h1.col-md-4 (str "Balance " (calc/balance events) "€")]
     [:h1.col-md-4 (str "Expenses " (calc/expenses events) "€")]
     [:h1.col-md-4 (str "Income " (calc/income events) "€")]]))

(defn labelled-radio-button [value type]
  [:label.btn.btn-primary
   (when (= value (get-in @form-data [:selected-filters :type]))
     {:class "active"})
   (string/capitalize value)
   [:input {:type "radio"
            :value value
            :id value
            :name type
            :on-click #(swap! form-data assoc-in [:selected-filters :type] value)}]])

(defn month-filter [events]
  [:select {:on-change #(swap! form-data assoc-in [:selected-filters :month] (-> % .-target .-value))}
   [:option "All-time"]
   (for [month (filters/months events)]
     ^{:key month}
     [:option month])])

(defn event-type-selector []
  [:div.btn-group {:data-toggle "buttons"}
   [labelled-radio-button "all" "type"]
   [labelled-radio-button "expenses" "type"]
   [labelled-radio-button "incomes" "type"]])

(defn filter-selector [events]
  [:form
   [month-filter events]
   [event-type-selector]])

(defn amount->pretty-string [amount]
  (str (/ amount 100) "€"))

(defn bank-event-table [events]
  [:div.table-responsive
   [:table.table.table-striped
    [:thead
     [:tr
      [:th "Date"]
      [:th "Amount"]
      [:th "Recipient"]]]
    [:tbody (for [event events]
              ^{:key (:id event)}
              [:tr
               [:td (str (:transaction_date event))]
               [:td (amount->pretty-string (:amount event))]
               [:td (str (:recipient event))]])]]])

(defn- event-retrieval-handler [e]
  (.preventDefault e)
  (get-events))

(defn event-retrieval-form [form-data]
  [:form.form-inline {:on-submit event-retrieval-handler}
   [:label {:for "events-username"} "Username"]
   [:input {:class "form-control"
            :id "events-username"
            :type "text"
            :value (:username @form-data)
            :on-change #(swap! form-data assoc :username (-> % .-target .-value))}]
   [:input {:type "submit"
            :class "btn btn-primary"
            :value "Get events"}]])

(defn- events-for-month [events month]
  (let [month-filter (filters/month-filter month)]
    (filter month-filter events)))

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
              [:li (str (:transaction_date occurrence))])]])])))

(defn recurring-expense-info [recurring-expenses]
  [:div.list-group
   (for [expense (re/sort-recurring-events recurring-expenses)]
     ^{:key (:id expense)}
     [recurring-expense-item expense])])

(defn events-page []
  (fn []
    (let [applied-filters (filters/combined-filter (:selected-filters @form-data))
          filtered-events (filter applied-filters @response-data)
          selected-month (get-in @form-data [:selected-filters :month])
          events-for-balance-info (if (= "All-time" selected-month)
                                    @response-data
                                    (events-for-month @response-data selected-month))]
      [:div.container
       [event-retrieval-form form-data]
       [filter-selector @response-data]
       [balance-info events-for-balance-info]
       [:div.container-fluid
        [:div.col-md-8
         [:h1 "Events"]
         [bank-event-table filtered-events]]
        [:div.col-md-4
         [:h1 "Recurring expenses"]
         [recurring-expense-info @recurring-expenses]]]])))
