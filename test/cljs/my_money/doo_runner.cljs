(ns my-money.doo-runner
  (:require [doo.runner :refer-macros [doo-tests]]
            [my-money.core-test]
            [my-money.calculations-test]
            [my-money.event-filters-test]
            [my-money.recurring-events-test]
            [my-money.app.controller.events-test]
            [my-money.app.controller.config-test]
            [my-money.app.controller.authentication-test]
            [my-money.app.controller.navigation-test]
            [my-money.app.controller.alerts-test]
            [my-money.views.events-test]
            [my-money.utils-test]))

(doo-tests 'my-money.core-test
           'my-money.calculations-test
           'my-money.event-filters-test
           'my-money.recurring-events-test
           'my-money.app.controller.events-test
           'my-money.app.controller.config-test
           'my-money.app.controller.authentication-test
           'my-money.app.controller.navigation-test
           'my-money.app.controller.alerts-test
           'my-money.views.events-test
           'my-money.utils-test)
