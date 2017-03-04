(ns my-money.events
    (:require [reagent.core :as r]
              [ajax.core :refer [GET]]))

(def response-data (r/atom nil))

(def username (r/atom nil))

(defn handle-response [response]
  (.log js/console (str response))
  (reset! response-data response))

(defn get-events []
  (GET "/events" {:handler handle-response
                  :params {:user @username}}))

(defn bank-event-table [events]
    [:div.table-responsive
     [:table.table.table-striped
      [:thead
       [:tr (for [heading (keys(first events))]
              [:th (str heading)])]]
      [:tbody (for [event (rest events)]
                [:tr (for [cell (vals event)]
                       [:td (str cell)])])]]])

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
   [bank-event-table @response-data]
   [:p @response-data]])
