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
