(ns my-money.calculations-test
  (:require [cljs.test :refer-macros [is are deftest testing use-fixtures]]
            [pjstadig.humane-test-output]
            [my-money.calculations :as calc]))

(deftest test-balance
  (is (= 5 (calc/balance [{:amount 300}
                          {:amount -100}
                          {:amount 150}
                          {:amount 150}]))))

(deftest test-expenses
  (is (= -1 (calc/expenses [{:amount 300}
                            {:amount -100}
                            {:amount 150}
                            {:amount 150}] #{}))))
(deftest test-income
  (is (= 6 (calc/income [{:amount 300}
                         {:amount -100}
                         {:amount 150}
                         {:amount 150}]))))

(deftest test-savings
  (is (= 1 (calc/savings [{:amount 300}
                          {:amount -100 :recipient "Savings account"}
                          {:amount 150}
                          {:amount 150}] #{"Savings account"}))))

(deftest test-expenses-with-savings
  (is (= -3 (calc/expenses [{:amount -300}
                            {:amount -100 :recipient "Savings account"}
                            {:amount 150}
                            {:amount 150}] #{"Savings account"}))))

(deftest test-sum-til-date
  (let [events [{:amount 300 :transaction_date (js/Date. 2016 8 1)}
                {:amount -100 :transaction_date (js/Date. 2017 4 1)}
                {:amount 150  :transaction_date (js/Date. 2017 5 1)}
                {:amount 150  :transaction_date (js/Date. 2017 6 1)}]]
    (is (= 300 (calc/sum-til-date (js/Date. 2016 8 1) events)))
    (is (= 200 (calc/sum-til-date (js/Date. 2017 4 1) events)))
    (is (= 350 (calc/sum-til-date (js/Date. 2017 5 1) events)))
    (is (= 500 (calc/sum-til-date (js/Date. 2017 6 1) events)))))
