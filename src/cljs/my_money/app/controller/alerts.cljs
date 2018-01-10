(ns my-money.app.controller.alerts
  (:require [tuck.core :as tuck]))

(defn add-alert [app alert-text time-stamp]
  (update app :alerts conj {:string alert-text
                            :timestamp time-stamp}))

(defrecord AddAlert [alert-text])
(defrecord RemoveAlert [alert])

(extend-protocol tuck/Event
  AddAlert
  (process-event [{alert-text :alert-text} app]
    (add-alert app alert-text (.getTime (js/Date.))))

  RemoveAlert
  (process-event [{alert :alert} app]
    (update app :alerts (partial remove #(= alert %)))))
