(ns my-money.app.state
  (:require [reagent.core :as r]))

(def initial-state {:events '()
                    :recurring-expenses '()
                    :starting-amount 0
                    :recipients #{}
                    :filters {:type "all"
                              :month "All-time"}})

(defonce app (r/atom initial-state))
