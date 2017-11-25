(ns my-money.app.controller.events-test
  (:require [cljs.test :refer-macros [is are deftest testing use-fixtures]]
            [my-money.app.controller.events :as ce]
            [tuck.core :as tuck]))

(def test-events '({:transaction_id "20171121/049003/946733"
                    :user_id 1
                    :amount 5000
                    :recipient "Vincent Adultman"
                    :id 687}))

(deftest test-settings-events
  (is (= {:events test-events}
         (tuck/process-event (ce/->SetEvents test-events)
                             {}))))

