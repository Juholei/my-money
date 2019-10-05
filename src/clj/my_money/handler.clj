(ns my-money.handler
  (:require [compojure.core :refer [routes wrap-routes]]
            [my-money.layout :refer [error-page]]
            [my-money.routes.auth :refer [auth-routes]]
            [my-money.routes.home :refer [home-routes]]
            [my-money.routes.upload :refer [upload-routes]]
            [my-money.routes.events :refer [events-routes]]
            [my-money.routes.config :refer [config-routes]]
            [compojure.route :as route]
            [my-money.env :refer [defaults]]
            [mount.core :as mount]
            [my-money.middleware :as middleware]
            [reitit.ring :as ring]
            [reitit.swagger :as swagger]
            [reitit.swagger-ui :as swagger-ui]))

(mount/defstate init-app
                :start ((or (:init defaults) identity))
                :stop  ((or (:stop defaults) identity)))

(def reitit-app
  (ring/ring-handler
   (ring/router
    [["/api"
      ["/ping" {:get {:summary "Testi"
                      :handler (fn [_]
                                 {:status 200
                                  :body "hello"})}}]]
     #_["/swagger.json"
      {:get {:no-doc true
             :swagger {:info {:title "my-api"}
                       :basePath "/"} ;; prefix for all paths
             :handler (swagger/create-swagger-handler)}}]])
   (swagger-ui/create-swagger-ui-handler {:path "/api-docs"})))

(def app-routes
  (routes
   (-> #'home-routes
       (wrap-routes middleware/wrap-csrf)
       (wrap-routes middleware/wrap-formats))
   (-> #'auth-routes
       (wrap-routes middleware/wrap-formats))
   (-> #'upload-routes
       (wrap-routes middleware/wrap-csrf)
       (wrap-routes middleware/wrap-restricted)
       (wrap-routes middleware/wrap-formats))
   (-> #'events-routes
       (wrap-routes middleware/wrap-csrf)
       (wrap-routes middleware/wrap-restricted)
       (wrap-routes middleware/wrap-formats))
   (-> #'config-routes
       (wrap-routes middleware/wrap-csrf)
       (wrap-routes middleware/wrap-restricted)
       (wrap-routes middleware/wrap-formats))
   reitit-app
   (route/not-found
    (:body
     (error-page {:status 404
                  :title "page not found"})))))

(defn app [] (middleware/wrap-base #'app-routes))
