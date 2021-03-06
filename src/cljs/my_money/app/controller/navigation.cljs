(ns my-money.app.controller.navigation
  (:require [tuck.core :as tuck]))

(defn set-in-progress [app in-progress?]
  (assoc app :in-progress in-progress?))

(defrecord OpenModal [modal])
(defrecord CloseModal [])
(defrecord SelectEventPage [page])
(defrecord SetShowAllEvents [show-all?])

(extend-protocol tuck/Event
  OpenModal
  (process-event [{modal :modal} app]
    (assoc app :modal modal))

  CloseModal
  (process-event [_ app]
    (dissoc app :modal))

  SelectEventPage
  (process-event [{page :page} app]
    (assoc app :event-page page))

  SetShowAllEvents
  (process-event [{show-all? :show-all?} app]
    (assoc app :show-all-events? show-all?)))
