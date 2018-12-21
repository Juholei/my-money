(ns my-money.app.controller.events
  (:require [my-money.ajax :as ajax]
            [my-money.app.controller.common :as c]
            [my-money.app.controller.navigation :as nc]
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
    (tuck/fx (nc/set-in-progress app true)
             {:tuck.effect/type ::ajax/get
              :url              "/events"
              :on-success       ->SetEvents
              :on-error         c/->ErrorHandler}))

  SetEvents
  (process-event [{events :events} app]
    (-> app
      (nc/set-in-progress false)
      (assoc :events events)))

  RetrieveRecurringExpenses
  (process-event [_ app]
    (tuck/fx app
             {:tuck.effect/type ::ajax/get
              :url              "/events/recurring/expenses"
              :on-success       ->SetRecurringExpenses
              :on-error         c/->ErrorHandler}))

  SetRecurringExpenses
  (process-event [{expenses :expenses} app]
    (assoc app :recurring-expenses expenses))

  SelectMonth
  (process-event [{month :month} app]
    (assoc-in app [:filters :month] month))

  SelectType
  (process-event [{type :type} app]
    (assoc-in app [:filters :type] type)))
