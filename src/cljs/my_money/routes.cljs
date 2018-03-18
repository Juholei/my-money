(ns my-money.routes
  (:require [bide.core :as r]
            [my-money.app.controller.navigation :as nc]))

(def router
  (r/router [["/" :home]
             ["/login" :login]
             ["/register" :registration]]))

(defn on-navigate
  "A function which will be called on each route change."
  [e! name params query]
  (println "Route change to: " name params query)
  (when (not= :home name)
    (e! (nc/->OpenModal name))))

(defn start! [e!]
  (r/start! router {:default     :home
                    :on-navigate (partial on-navigate e!)}))

(defn navigate!
  ([page]
    (navigate! page nil))
  ([page params]
   (r/navigate! router page params)))
