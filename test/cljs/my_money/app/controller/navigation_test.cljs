(ns my-money.app.controller.navigation-test
  (:require [cljs.test :refer-macros [is are deftest testing use-fixtures]]
            [my-money.app.state :as state]
            [my-money.app.controller.navigation :as nc]
            [tuck.core :as tuck]))

(deftest test-set-in-progress
  (testing "Setting in progress to true"
    (is (= true
           (:in-progress (nc/set-in-progress state/initial-state true)))))
  (testing "Setting in progress to false"
    (is (= false
           (:in-progress (nc/set-in-progress state/initial-state false))))))

(deftest test-modal
  (testing "OpenModal sets :modal in state"
    (is (= :test-modal
           (:modal (tuck/process-event (nc/->OpenModal :test-modal) state/initial-state)))))
  (testing "CloseModal clears :modal in state"
    (is (= nil
           (->> state/initial-state
                (tuck/process-event (nc/->OpenModal :test-modal))
                (tuck/process-event (nc/->CloseModal))
                :modal)))))
