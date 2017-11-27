(ns my-money.app.controller.events
  (:require [ajax.core :refer [GET]]
            [my-money.app.state :as state]
            [tuck.core :as tuck]))

(defrecord RetrieveEvents [])
(defrecord SetEvents [events])
(defrecord RetrieveRecurringExpenses [])
(defrecord SetRecurringExpenses [expenses])
(defrecord SelectMonth [month])
(defrecord SelectType [type])

(extend-protocol tuck/Event
  RetrieveEvents
  (process-event [_ app]
    (tuck/action! (fn [e!]
                    (GET "/events" {:handler #(e! (->SetEvents %))})))
    app)

  SetEvents
  (process-event [{events :events} app]
    (assoc app :events events))

  RetrieveRecurringExpenses
  (process-event [_ app]
    (tuck/action! (fn [e!]
                    (GET "/events/recurring/expenses"
                         {:handler #(e! (->SetRecurringExpenses %))})))
    app)

  SetRecurringExpenses
  (process-event [{expenses :expenses} app]
    (assoc app :recurring-expenses expenses))

  SelectMonth
  (process-event [{month :month} app]
    (assoc-in app [:filters :month] month))

  SelectType
  (process-event [{type :type} app]
    (assoc-in app [:filters :type] type)))