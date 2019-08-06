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
  (is (= {:events test-events
          :in-progress false}
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
                    :type "all"}
          :in-progress false}
         (->> {}
              (tuck/process-event (ce/->SetEvents test-events))
              (tuck/process-event (ce/->SetRecurringExpenses test-expenses))
              (tuck/process-event (ce/->SelectMonth "2.2017"))
              (tuck/process-event (ce/->SelectType "all"))))))

(deftest test-SelectEvent
  (testing "Selecting single event returns state with that event in selected-events set"
    (is (= {:selected-events #{{:id "123"}}}
           (->> {:selected-events #{}}
                (tuck/process-event (ce/->SelectEvent {:id "123"} true))))))

  (testing "Selecting single event  and then deselecting it returns state with empty selected-events set"
    (is (= {:selected-events #{}}
           (->> {:selected-events #{}}
                (tuck/process-event (ce/->SelectEvent {:id "123"} true))
                (tuck/process-event (ce/->SelectEvent {:id "123"} false))))))
  (testing "Selecting single event multiple times add it only once"
    (is (= {:selected-events #{{:id "123"}}}
           (->> {:selected-events #{}}
                (tuck/process-event (ce/->SelectEvent {:id "123"} true))
                (tuck/process-event (ce/->SelectEvent {:id "123"} true))))))
  (testing "Selecting two events results in two events in the set"
    (is (= {:selected-events #{{:id "123"} {:id "124"}}}
           (->> {:selected-events #{}}
                (tuck/process-event (ce/->SelectEvent {:id "123"} true))
                (tuck/process-event (ce/->SelectEvent {:id "124"} true)))))))

(deftest test-ClearSelectedEvents
  (testing "Populated selected-events set is replaced with empty set"
    (is (= {:selected-events #{}}
           (->> {:selected-events #{{:id "123"} {:id "124"}}}
                (tuck/process-event (ce/->ClearSelectedEvents)))))))
