(ns my-money.app.controller.config
  (:require [ajax.core :refer [GET POST]]
            [my-money.app.state :as state]
            [reagent.session :as session]
            [tuck.core :as tuck]))

(defn get-config []
  (GET "/get-config" {:handler #(swap! state/app merge %)}))

(defrecord SaveConfig [config])
(defrecord ConfigSaved [])

(extend-protocol tuck/Event
  SaveConfig
  (process-event [{config :config} app]
    (tuck/action! (fn [e!]
                    (let [starting-amount-changed? (not= (* (:starting-amount config) 100)
                                                         (:starting-amount app))]
                      (POST "/save-config"
                            {:params (if starting-amount-changed?
                                       config
                                       (dissoc config :starting-amount))
                             :handler #(e! (->ConfigSaved))}))))
    app)

  ConfigSaved
  (process-event [_ app]
    (tuck/action! (fn [e!]
                    (session/remove! :modal)
                    (get-config)))
    app))
