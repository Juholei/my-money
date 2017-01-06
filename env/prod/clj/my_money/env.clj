(ns my-money.env
  (:require [clojure.tools.logging :as log]))

(def defaults
  {:init
   (fn []
     (log/info "\n-=[my-money started successfully]=-"))
   :stop
   (fn []
     (log/info "\n-=[my-money has shut down successfully]=-"))
   :middleware identity})
