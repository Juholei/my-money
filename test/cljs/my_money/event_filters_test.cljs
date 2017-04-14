(ns my-money.event-filters-test
  (:require [cljs.test :refer-macros [is are deftest testing use-fixtures]]
            [pjstadig.humane-test-output]
            [reagent.core :as reagent :refer [atom]]
            [my-money.event-filters :as filters]))

(deftest test-months-from-events
  (is (= #{"01.2017" "02.2017"}
         (filters/months [{:transaction_date "01.01.2017"}
                          {:transaction_date "11.01.2017"}
                          {:transaction_date "10.02.2017"}
                          {:transaction_date "01.02.2017"}]))))

(def events [{:amount 100}
             {:amount -100}
             {:amount 200}])

(deftest test-only-income-events
  (let [income-filter (filters/event-filter "incomes")]
    (is (= [{:amount 100}
            {:amount 200}]
           (filter income-filter events)))))

(deftest test-only-expense-events
  (let [expense-filter (filters/event-filter "expenses")]
    (is (= [{:amount -100}]
           (filter expense-filter events)))))

(deftest test-all-events
  (let [all-filter (filters/event-filter "all")]
    (is (= [{:amount 100}
            {:amount -100}
            {:amount 200}]
           (filter all-filter events)))))