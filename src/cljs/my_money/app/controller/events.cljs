(ns my-money.app.controller.events
  (:require [ajax.core :refer [GET]]
            [my-money.app.state :as state]))

(defn recurring-expenses-handler [response]
  (swap! state/app assoc :recurring-expenses response))

(defn get-recurring-expenses []
  (GET "/events/recurring/expenses"
       {:handler recurring-expenses-handler}))

(defn handle-response [response]
  (swap! state/app assoc :events response))

(defn get-config []
  (GET "/get-config" {:handler #(swap! state/app merge %)}))

(defn get-events []
  (get-recurring-expenses)
  (GET "/events" {:handler handle-response})
  (get-config))

