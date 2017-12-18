(ns my-money.app.controller.events
  (:require [ajax.core :refer [GET]]
            [my-money.app.controller.navigation :as nc]
            [my-money.app.state :as state]
            [tuck.core :as tuck]))

(defrecord RetrieveEvents [])
(defrecord SetEvents [events])
(defrecord RetrieveRecurringExpenses [])
(defrecord SetRecurringExpenses [expenses])
(defrecord SelectMonth [month])
(defrecord SelectType [type])
(defrecord ErrorHandler [])

(extend-protocol tuck/Event
  RetrieveEvents
  (process-event [_ app]
    (tuck/action! (fn [e!]
                    (GET "/events" {:handler #(e! (->SetEvents %))
                                    :error-handler #(e! (->ErrorHandler))})))
    (nc/set-in-progress app true))

  SetEvents
  (process-event [{events :events} app]
    (-> app
      (nc/set-in-progress false)
      (assoc :events events)))

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
    (assoc-in app [:filters :type] type))

  ErrorHandler
  (process-event [_ app]
                 (nc/set-in-progress app false)))
