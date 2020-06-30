(ns my-money.recurring-events-test
  (:require [cljs.test :refer-macros [is are deftest testing use-fixtures]]
            [my-money.recurring-events :as re]))

(def test-data '({:amount -2468
                  :recipient "DNA OY"
                  :transaction_date "09.04.2017"}
                 {:amount -2468
                  :recipient "DNA OY"
                  :transaction_date "09.03.2017"}
                 {:amount -1200
                  :recipient "Stuff Inc"
                  :transaction_date "09.04.2017"}))

(def correct-output [{:recipient "DNA OY"
                      :amount -2468
                      :events [{:amount -2468
                                :recipient "DNA OY"
                                :transaction_date "09.04.2017"}
                               {:amount -2468
                                :recipient "DNA OY"
                                :transaction_date "09.03.2017"}]}
                     {:recipient "Stuff Inc"
                      :amount -1200
                      :events [{:amount -1200
                                :recipient "Stuff Inc"
                                :transaction_date "09.04.2017"}]}])
(deftest test-sorting
  (is (= correct-output (re/sort-recurring-events test-data))))
