(ns user
  (:require [mount.core :as mount]
            [my-money.figwheel :refer [start-fw stop-fw cljs]]
            my-money.core))

(defn start []
  (mount/start-without #'my-money.core/repl-server))

(defn stop []
  (mount/stop-except #'my-money.core/repl-server))

(defn restart []
  (stop)
  (start))


