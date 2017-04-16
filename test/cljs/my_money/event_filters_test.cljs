(ns my-money.event-filters-test
  (:require [cljs.test :refer-macros [is are deftest testing use-fixtures]]
            [pjstadig.humane-test-output]
            [reagent.core :as reagent :refer [atom]]
            [my-money.event-filters :as filters]))

(def month-filtering-test-events [{:transaction_date "01.01.2017"}
                                  {:transaction_date "11.01.2017"}
                                  {:transaction_date "10.02.2017"}
                                  {:transaction_date "01.02.2017"}])

(def type-filtering-test-events [{:amount 100}
             {:amount -100}
             {:amount 200}])

(def all-filters-test-events [{:transaction_date "01.01.2017"
                               :amount 200}
                              {:transaction_date "11.01.2017"
                               :amount :100}
                              {:transaction_date "10.02.2017"
                               :amount -150}
                              {:transaction_date "01.02.2017"
                               :amount 1230}
                              {:transaction_date "31.12.2016" :amount 5000}])

(deftest test-months-from-events
  (is (= #{"01.2017" "02.2017"}
         (filters/months month-filtering-test-events))))

(deftest test-only-income-events
  (let [income-filter (filters/event-type-filter "incomes")]
    (is (= [{:amount 100}
            {:amount 200}]
           (filter income-filter type-filtering-test-events)))))

(deftest test-only-expense-events
  (let [expense-filter (filters/event-type-filter "expenses")]
    (is (= [{:amount -100}]
           (filter expense-filter type-filtering-test-events)))))

(deftest test-all-events
  (let [all-filter (filters/event-type-filter "all")]
    (is (= [{:amount 100}
            {:amount -100}
            {:amount 200}]
           (filter all-filter type-filtering-test-events)))))

(deftest test-month-filtering
  (let [month-filter (filters/month-filter "01.2017")]
    (is (= [{:transaction_date "01.01.2017"}
            {:transaction_date "11.01.2017"}]
           (filter month-filter month-filtering-test-events)))))

(deftest test-applying-all-filters
  (let [combined-filter (filters/combined-filter {:month "02.2017" :type "incomes"})]
    (is (= [{:transaction_date "01.02.2017" :amount 1230}]
           (filter combined-filter all-filters-test-events)))))

(deftest test-applying-all-filters-but-no-matches
  (let [combined-filter (filters/combined-filter {:month "01.2017" :type "expenses"})]
    (is (= [] (filter combined-filter all-filters-test-events)))))
