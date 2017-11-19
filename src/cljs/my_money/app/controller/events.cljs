(ns my-money.app.controller.events
  (:require [ajax.core :refer [GET]]
            [my-money.app.state :as state]
            [tuck.core :as tuck]))

(defn recurring-expenses-handler [response]
  (swap! state/app assoc :recurring-expenses response))

(defn get-recurring-expenses []
  (GET "/events/recurring/expenses"
       {:handler recurring-expenses-handler}))

(defn handle-response [response]
  (swap! state/app assoc :events response))

(defrecord GetEvents [])
(defrecord GetEventsResult [events])

(extend-protocol tuck/Event
  GetEvents
  (process-event [_ app]
    (tuck/action! (fn [e!]
                    (get-recurring-expenses)
                    (GET "/events" {:handler #(e! (->GetEventsResult %))})))
    app)

  GetEventsResult
  (process-event [{events :events} app]
    (assoc app :events events)))
