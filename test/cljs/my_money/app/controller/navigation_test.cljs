(ns my-money.app.controller.navigation-test
  (:require [cljs.test :refer-macros [is are deftest testing use-fixtures]]
            [my-money.app.state :as state]
            [my-money.app.controller.navigation :as nc]))

(deftest test-set-in-progress
  (testing "Setting in progress to true"
    (is (= true
           (:in-progress (nc/set-in-progress state/initial-state true)))))
  (testing "Setting in progress to false"
    (is (= false
           (:in-progress (nc/set-in-progress state/initial-state false))))))