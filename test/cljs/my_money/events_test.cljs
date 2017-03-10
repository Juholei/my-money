(ns my-money.events-test
  (:require [cljs.test :refer-macros [is are deftest testing use-fixtures]]
            [pjstadig.humane-test-output]
            [reagent.core :as reagent :refer [atom]]
            [my-money.events :as events]))

(deftest test-balance
  (is (= 5 (events/balance [{:amount 300}
                            {:amount -100}
                            {:amount 150}
                            {:amount 150}]))))

(deftest test-expenses
  (is (= -1 (events/expenses [{:amount 300}
                              {:amount -100}
                              {:amount 150}
                              {:amount 150}]))))
(deftest test-income
  (is (= 6 (events/income [{:amount 300}
                           {:amount -100}
                           {:amount 150}
                           {:amount 150}]))))
