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
                    :chart :trend})

(defonce app (r/atom initial-state))
