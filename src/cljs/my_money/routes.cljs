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

(def restricted-routes #{:config :upload})

(defn not-allowed? [route]
  (and (not (session/get :identity))
       (restricted-routes route)))

(defmulti navigate
  (fn [key _]
    (if (not-allowed? key)
      :login
      key)))

(defmethod navigate :login [_ e!]
  (e! (nc/->OpenModal :login)))

(defmethod navigate :home [_ e!]
  (e! (nc/->CloseModal)))

(defmethod navigate :default [key e!]
  (e! (nc/->OpenModal key)))

(defn on-navigate
  "A function which will be called on each route change."
  [e! name params query]
  (navigate name e!))

(defn start! [e!]
  (r/start! router {:default     :home
                    :on-navigate (partial on-navigate e!)}))

(defn navigate!
  ([page]
    (navigate! page nil))
  ([page params]
   (r/navigate! router page params)))
