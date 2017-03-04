(ns my-money.handler
  (:require [compojure.core :refer [routes wrap-routes]]
            [my-money.layout :refer [error-page]]
            [my-money.routes.home :refer [home-routes]]
            [my-money.routes.upload :refer [upload-routes]]
            [my-money.routes.events :refer [events-routes]]
            [compojure.route :as route]
            [my-money.env :refer [defaults]]
            [mount.core :as mount]
            [my-money.middleware :as middleware]))

(mount/defstate init-app
                :start ((or (:init defaults) identity))
                :stop  ((or (:stop defaults) identity)))

(def app-routes
  (routes
    (-> #'home-routes
        (wrap-routes middleware/wrap-csrf)
        (wrap-routes middleware/wrap-formats))
    (-> #'upload-routes
        (wrap-routes middleware/wrap-csrf)
        (wrap-routes middleware/wrap-formats))
    (-> #'events-routes
        (wrap-routes middleware/wrap-csrf)
        (wrap-routes middleware/wrap-formats))
    (route/not-found
      (:body
        (error-page {:status 404
                     :title "page not found"})))))


(defn app [] (middleware/wrap-base #'app-routes))
