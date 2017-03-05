(ns my-money.events
    (:require [reagent.core :as r]
              [ajax.core :refer [GET]]))

(def response-data (r/atom nil))

(def username (r/atom nil))

(defn handle-response [response]
  (reset! response-data response))

(defn get-events []
  (GET "/events" {:handler handle-response
                  :params {:user @username}}))

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
               [:td (str (/ (.-rep (:amount event)) 100) "€")]
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
   [bank-event-table @response-data]])
