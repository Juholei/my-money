(ns my-money.app.controller.config
  (:require [ajax.core :refer [GET POST]]
            [my-money.app.state :as state]
            [reagent.session :as session]
            [tuck.core :as tuck]))

(defrecord SaveConfig [config])
(defrecord ConfigSaved [])
(defrecord RetrieveConfig [])
(defrecord SetConfig [config])

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
                    (e! (->RetrieveConfig))))
    app)

  RetrieveConfig
  (process-event [_ app]
    (tuck/action! (fn [e!]
                    (GET "/get-config" {:handler #(swap! state/app merge %)})))
    app)

  SetConfig
  (process-event [{config :config} app]
    (merge app config)))