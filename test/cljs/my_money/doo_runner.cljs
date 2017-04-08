(ns my-money.doo-runner
  (:require [doo.runner :refer-macros [doo-tests]]
            [my-money.core-test]
            [my-money.calculations-test]
            [my-money.event-filters-test]))

(doo-tests 'my-money.core-test
           'my-money.calculations-test
           'my-money.event-filters-test)

