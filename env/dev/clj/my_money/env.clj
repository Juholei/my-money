(ns my-money.env
  (:require [selmer.parser :as parser]
            [clojure.tools.logging :as log]
            [my-money.dev-middleware :refer [wrap-dev]]))

(def defaults
  {:init
   (fn []
     (parser/cache-off!)
     (log/info "\n-=[my-money started successfully using the development profile]=-"))
   :stop
   (fn []
     (log/info "\n-=[my-money has shut down successfully]=-"))
   :middleware wrap-dev})
