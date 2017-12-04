(ns my-money.doo-runner
  (:require [doo.runner :refer-macros [doo-tests]]
            [my-money.core-test]
            [my-money.calculations-test]
            [my-money.event-filters-test]
            [my-money.recurring-events-test]
            [my-money.app.controller.events-test]
            [my-money.app.controller.config-test]))

(doo-tests 'my-money.core-test
           'my-money.calculations-test
           'my-money.event-filters-test
           'my-money.recurring-events-test
           'my-money.app.controller.events-test
           'my-money.app.controller.config-test)
