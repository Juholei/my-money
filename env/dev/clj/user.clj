(ns user
  (:require [mount.core :as mount]
            my-money.core))

(defn start []
  (mount/start-without #'my-money.core/repl-server))

(defn stop []
  (mount/stop-except #'my-money.core/repl-server))

(defn restart []
  (stop)
  (start))


