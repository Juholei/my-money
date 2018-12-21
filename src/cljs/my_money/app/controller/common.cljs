(ns my-money.app.controller.common
  (:require [tuck.core :as tuck :refer-macros [define-event]]
            [my-money.app.controller.navigation :as nc]))

(defrecord ErrorHandler [error])

(extend-protocol tuck/Event
  ErrorHandler
  (process-event [_ app]
    (nc/set-in-progress app false)))
