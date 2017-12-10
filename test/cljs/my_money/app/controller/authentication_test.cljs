(ns my-money.app.controller.authentication-test
  (:require [cljs.test :refer-macros [is are deftest testing use-fixtures]]
            [my-money.app.state :as state]
            [my-money.app.controller.authentication :as ac]
            [tuck.core :as tuck]))


(deftest logout-sets-state-to-initial-state
  (is (= state/initial-state
         (tuck/process-event (ac/->LogoutSucceeded)
                             {}))))
