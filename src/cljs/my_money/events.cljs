(ns my-money.events
    (:require [clojure.string :as string]
              [reagent.core :as r]
              [ajax.core :refer [GET]]
              [my-money.calculations :as calc]
              [my-money.event-filters :as filters]))

(defonce response-data (r/atom nil))

(defonce form-data (r/atom {:username ""
                            :selected-filter "all"}))

(defn handle-response [response]
  (reset! response-data response))

(defn get-events
  ([]
   (get-events (:username @form-data)))
  ([username]
   (GET "/events" {:handler handle-response
                  :params {:user username}})))

(defn balance-info [events]
  (when events
    [:div.container
     [:h1.col-md-4 (str "Balance " (calc/balance events) "€")]
     [:h1.col-md-4 (str "Expenses " (calc/expenses events) "€")]
     [:h1.col-md-4 (str "Income " (calc/income events) "€")]]))

(defn labelled-radio-button [value type]
  [:div.radio
   [:input {:type "radio"
            :value value
            :id value
            :name type
            :on-click #(swap! form-data assoc :selected-filter value)}]
   [:label {:for value} (string/capitalize value)]])

(defn month-filter [events]
  [:select
   [:option "All-time"]
   (for [month (filters/months events)]
     ^{:key month}
     [:option month])])

(defn filter-selector [events]
  [:form
   [month-filter events]
   [labelled-radio-button "all" "type"]
   [labelled-radio-button "expenses" "type"]
   [labelled-radio-button "incomes" "type"]])

(defn bank-event-table [events]
  [:div.table-responsive
   [:table.table.table-striped
    [:thead
     [:tr
      [:th "Date"]
      [:th "Amount"]
      [:th "Recipient"]]]
    [:tbody (for [event (rest events)]
              ^{:key (:id event)}
              [:tr
               [:td (str (:transaction_date event))]
               [:td (str (/ (:amount event) 100) "€")]
               [:td (str (:recipient event))]])]]])

(defn events-page []
  [:div.container
   [:form.form-inline {:on-submit #(get-events)}
     [:label {:for "events-username"} "Username"]
     [:input {:class "form-control"
              :id "events-username"
              :type "text"
              :value (:username @form-data)
              :on-change #(swap! form-data assoc :username (-> % .-target .-value))}]
     [:input {:type "submit"
              :class "btn btn-primary"
              :value "Get events"}]]
   [filter-selector @response-data]
   [balance-info @response-data]
   [bank-event-table (filter #((filters/event-type->filter (:selected-filter @form-data)) (:amount %)) @response-data)]])
