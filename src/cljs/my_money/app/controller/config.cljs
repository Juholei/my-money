(ns my-money.app.controller.config
  (:require [ajax.core :refer [GET POST]]
            [my-money.app.state :as state]
            [reagent.session :as session]))

(defn get-config []
  (GET "/get-config" {:handler #(swap! state/app merge %)}))

(defn- config-saved []
  (get-config)
  (session/remove! :modal))

(defn save-config! [config]
  (let [starting-amount-changed? (not= (* (:starting-amount @config) 100)
                                       (:starting-amount @state/app))]
    (POST "/save-config"
          {:params (if starting-amount-changed?
                     @config
                     (dissoc @config :starting-amount))
           :handler config-saved})))
