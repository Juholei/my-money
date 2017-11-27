(ns my-money.app.state
  (:require [reagent.core :as r]))

(defonce app (r/atom {:events '()
                      :recurring-expenses '()
                      :starting-amount 0
                      :recipients #{}
                      :filters {:type "all"
                                :month "All-time"}}))