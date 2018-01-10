(ns my-money.app.controller.alerts-test
  (:require [cljs.test :refer-macros [is are deftest testing use-fixtures]]
            [my-money.app.state :as state]
            [my-money.app.controller.alerts :as ac]
            [tuck.core :as tuck]))

(def alert {:string    "Test alert!"
            :timestamp 1515602978994})

(def alert-to-be-removed {:string    "Test alert to be removed!"
                          :timestamp 1515602978996})

(def test-alert-state {:alerts (list alert alert-to-be-removed)})

(deftest test-adding-alert
  (testing "add-alert returns collection with the alert in it"
    (is (= (list alert)
           (:alerts (ac/add-alert {} "Test alert!" 1515602978994))))))

(deftest test-removing-alert
  (testing "RemoveAlert removes only the alert given to it"
    (is (= (list alert)
           (->> test-alert-state
                (tuck/process-event (ac/->RemoveAlert alert-to-be-removed))
                :alerts))))
  (testing "RemoveAlert does nothing if there isn't alert"
    (is (empty? (->> {}
                     (tuck/process-event (ac/->RemoveAlert alert-to-be-removed))
                     :alerts)))))
