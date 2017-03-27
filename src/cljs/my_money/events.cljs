(ns my-money.events
    (:require [reagent.core :as r]
              [ajax.core :refer [GET]]
              [my-money.calculations :as calc]))

(def response-data (r/atom nil))

(def username (r/atom nil))

(defn handle-response [response]
  (reset! response-data response))

(defn get-events []
  (GET "/events" {:handler handle-response
                  :params {:user @username}}))

(defn balance-info [events]
  (when events
    [:div.container
     [:h1.col-md-4 (str "Balance " (calc/balance events) "€")]
     [:h1.col-md-4 (str "Expenses " (calc/expenses events) "€")]
     [:h1.col-md-4 (str "Income " (calc/income events) "€")]]))

(defn bank-event-table [events]
  [:div.table-responsive
   [:table.table.table-striped
    [:thead
     [:tr
      [:th "Date"]
      [:th "Amount"]
      [:th "Recipient"]]]
    [:tbody (for [event (rest events)]
              [:tr
               [:td (str (:transaction_date event))]
               [:td (str (/ (:amount event) 100) "€")]
               [:td (str (:recipient event))]])]]])

(defn events-page []
  [:div.container
   [:form#formi {:on-submit get-events}
    [:div.form-inline
     [:label {:for "username"} "Username"]
     [:input {:class "form-control"
              :id "username"
              :type "text"
              :value @username
              :on-change #(reset! username (-> % .-target .-value))}]]
    [:input {:type "submit"
             :value "Get events"}]]
   [balance-info @response-data]
   [bank-event-table @response-data]])
