(ns my-money.doo-runner
  (:require [doo.runner :refer-macros [doo-tests]]
            [my-money.core-test]
            [my-money.events-test]))

(doo-tests 'my-money.core-test
           'my-money.events-test)

