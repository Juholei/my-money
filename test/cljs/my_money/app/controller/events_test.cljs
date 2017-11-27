(ns my-money.app.controller.events-test
  (:require [cljs.test :refer-macros [is are deftest testing use-fixtures]]
            [my-money.app.controller.events :as ce]
            [tuck.core :as tuck]))

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

(deftest test-settings-events
  (is (= {:events test-events}
         (tuck/process-event (ce/->SetEvents test-events)
                             {}))))

(deftest test-setting-recurring-expenses
  (is (= {:recurring-expenses test-expenses}
         (tuck/process-event (ce/->SetRecurringExpenses test-expenses) {}))))

(deftest test-setting-month-in-filters
  (is (= "2.2017"
         (-> (tuck/process-event (ce/->SelectMonth "2.2017") {})
             (get-in [:filters :month])))))

(deftest test-setting-event-type-in-filters
  (is (= "all"
         (-> (tuck/process-event (ce/->SelectType "all") {})
             (get-in [:filters :type])))))

(deftest test-setting-everything
  (is (= {:events test-events
          :recurring-expenses test-expenses
          :filters {:month "2.2017"
                    :type "all"}}
         (->> {}
              (tuck/process-event (ce/->SetEvents test-events))
              (tuck/process-event (ce/->SetRecurringExpenses test-expenses))
              (tuck/process-event (ce/->SelectMonth "2.2017"))
              (tuck/process-event (ce/->SelectType "all"))))))
