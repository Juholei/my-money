(ns my-money.views.events-test
  (:require [cljs.test :refer-macros [is are deftest testing use-fixtures]]
            [my-money.views.events :as events]))

(deftest test-paged-events-contains-all-events
  (let [events (mapv (partial hash-map :id) (range 101))]
    (is (= events
           (apply concat (events/events->pages events 10))))))
