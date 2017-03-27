(ns my-money.calculations-test
  (:require [cljs.test :refer-macros [is are deftest testing use-fixtures]]
            [pjstadig.humane-test-output]
            [reagent.core :as reagent :refer [atom]]
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
                              {:amount 150}]))))
(deftest test-income
  (is (= 6 (calc/income [{:amount 300}
                           {:amount -100}
                           {:amount 150}
                           {:amount 150}]))))
