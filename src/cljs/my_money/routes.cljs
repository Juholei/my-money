(ns my-money.routes
  (:require [bide.core :as r]
            [my-money.app.controller.navigation :as nc]
            [reagent.session :as session]))

(def router
  (r/router [["/" :home]
             ["/login" :login]
             ["/register" :registration]
             ["/config" :config]
             ["/upload" :upload]]))

(defn on-navigate
  "A function which will be called on each route change."
  [e! name params query]
  (if (and (not (session/get :identity))
           (#{:config :upload} name))
    (e! (nc/->OpenModal :login))
    (if (= name :home)
      (e! (nc/->CloseModal))
      (e! (nc/->OpenModal name)))))

(defn start! [e!]
  (r/start! router {:default     :home
                    :on-navigate (partial on-navigate e!)}))

(defn navigate!
  ([page]
    (navigate! page nil))
  ([page params]
   (r/navigate! router page params)))
