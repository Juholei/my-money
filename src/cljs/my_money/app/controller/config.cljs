(ns my-money.app.controller.config
  (:require [ajax.core :refer [POST]]
            [my-money.ajax :as ajax]
            [tuck.core :as tuck]
            [my-money.app.controller.navigation :as nc]
            [my-money.routes :as routes]))

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
    (nc/set-in-progress app true))

  ConfigSaved
  (process-event [_ app]
    (tuck/action! (fn [e!]
                    (e! (->RetrieveConfig))
                    (routes/navigate! :home)))
    (-> app
        (nc/set-in-progress false)
        (dissoc :modal)))

  RetrieveConfig
  (process-event [_ app]
    (tuck/fx app
             {:tuck.effect/type ::ajax/get
              :url              "/get-config"
              :on-success       ->SetConfig}))

  SetConfig
  (process-event [{config :config} app]
    (merge app config)))
