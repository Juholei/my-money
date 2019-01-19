(ns my-money.app.controller.config
  (:require [my-money.ajax :as ajax]
            [tuck.core :as tuck]
            [my-money.app.controller.navigation :as nc]
            [my-money.app.controller.common :as c]
            [my-money.routes :as routes]))

(defrecord SaveConfig [config])
(defrecord ConfigSaved [body])
(defrecord RetrieveConfig [])
(defrecord SetConfig [config])

(extend-protocol tuck/Event
  SaveConfig
  (process-event [{config :config} app]
    (let [starting-amount-changed? (not= (* (:starting-amount config) 100)
                                         (:starting-amount app))]
      (tuck/fx (nc/set-in-progress app true)
               {:tuck.effect/type ::ajax/post
                :url              "/save-config"
                :on-success       ->ConfigSaved
                :params           (if starting-amount-changed?
                                    config
                                    (dissoc config :starting-amount))
                :on-error         c/->ErrorHandler})))

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
              :on-success       ->SetConfig
              :on-error         c/->ErrorHandler}))

  SetConfig
  (process-event [{config :config} app]
    (merge app config)))
