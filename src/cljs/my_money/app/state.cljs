(ns my-money.app.state
  (:require [reagent.core :as r]))

(def initial-state {:events '()
                    :recurring-expenses '()
                    :starting-amount 0
                    :recipients #{}
                    :filters {:type "all"
                              :month "All-time"}
                    :in-progress false
                    :modal :login
                    :alerts '()
                    :event-page 0
                    :show-all-events? true
                    :selected-events #{}})

(defonce app (r/atom initial-state))
