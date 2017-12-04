(ns my-money.app.controller.config-test
  (:require [cljs.test :refer-macros [is are deftest testing use-fixtures]]
            [my-money.app.controller.config :as cc]
            [tuck.core :as tuck]))

(def test-config {:starting-amount 10000
                  :recipients #{"Bank" "Another Bank"}})

(def test-events '({:transaction_id "20171121/049003/946733"
                    :user_id 1
                    :amount 5000
                    :recipient "Vincent Adultman"
                    :id 687}))

(def test-expenses '({:transaction_id "20170821/049003/946733"
                      :user_id 1
                      :amount -5000
                      :recipient "Vincent Adultman"
                      :id 737}))

(def test-state {:events test-events
                 :recurring-expenses test-expenses
                 :filters {:month "2.2017"
                           :type "all"}
                 :starting-amount 10000})

(deftest test-setting-config
  (is (= test-config
         (tuck/process-event (cc/->SetConfig test-config)
                             {})))

  (is (= {:events test-events
          :recurring-expenses test-expenses
          :filters {:month "2.2017"
                    :type "all"}
          :starting-amount 10000
          :recipients #{"Bank" "Another Bank"}}
         (tuck/process-event (cc/->SetConfig {:starting-amount 10000
                                              :recipients #{"Bank" "Another Bank"}})
                             test-state))))
