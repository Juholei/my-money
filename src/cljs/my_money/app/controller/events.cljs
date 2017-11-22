(ns my-money.app.controller.events
  (:require [ajax.core :refer [GET]]
            [my-money.app.state :as state]
            [tuck.core :as tuck]))

(defrecord GetEvents [])
(defrecord GetEventsResult [events])
(defrecord GetRecurringExpenses [])
(defrecord GetRecurringExpensesResult [expenses])

(extend-protocol tuck/Event
  GetEvents
  (process-event [_ app]
    (tuck/action! (fn [e!]
                    (GET "/events" {:handler #(e! (->GetEventsResult %))})))
    app)

  GetEventsResult
  (process-event [{events :events} app]
    (assoc app :events events))

  GetRecurringExpenses
  (process-event [_ app]
    (tuck/action! (fn [e!]
                    (GET "/events/recurring/expenses"
                         {:handler #(e! (->GetRecurringExpensesResult %))})))
    app)

  GetRecurringExpensesResult
  (process-event [{expenses :expenses} app]
    (assoc app :recurring-expenses expenses)))
