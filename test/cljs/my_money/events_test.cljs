(ns my-money.events-test
  (:require [cljs.test :refer-macros [is are deftest testing use-fixtures]]
            [pjstadig.humane-test-output]
            [my-money.events :as events]))

(def events [{:amount 100}
             {:amount -100}
             {:amount 200}])

(deftest test-only-income-events
  (let [income-filter (events/event-filter "incomes")]
    (is (= [{:amount 100}
            {:amount 200}]
           (filter income-filter events)))))

(deftest test-only-expense-events
  (let [expense-filter (events/event-filter "expenses")]
    (is (= [{:amount -100}]
           (filter expense-filter events)))))

(deftest test-all-events
  (let [all-filter (events/event-filter "all")]
    (is (= [{:amount 100}
            {:amount -100}
            {:amount 200}]
           (filter all-filter events)))))