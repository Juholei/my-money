(ns my-money.app.controller.config
  (:require [ajax.core :refer [GET]]
    [my-money.app.state :as state]))

(defn get-config []
  (GET "/get-config" {:handler #(swap! state/app merge %)}))
