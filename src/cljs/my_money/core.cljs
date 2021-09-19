(ns my-money.core
  (:require [reagent.dom :as rdom]
            [reagent.session :as session]
            [my-money.ajax :refer [load-interceptors!]]
            [my-money.routes :as routes]
            [my-money.app.controller.alerts :as alerts-controller]
            [my-money.app.controller.config :as cc]
            [my-money.app.state :as state]
            [my-money.components.common :as c]
            [my-money.components.navbar :as navbar]
            [my-money.views.config :as config]
            [my-money.views.registration :as registration]
            [my-money.views.login :as login]
            [my-money.views.upload :as upload]
            [my-money.views.events :refer [events-page]]
            [tuck.core :as tuck]))

(defn modal-key->modal [key]
  (condp = key
    :registration registration/registration-form
    :login login/login-form
    :config config/config-modal
    :upload upload/upload-modal
    nil))

(defn modal [e! modal-key in-progress?]
  (when-let [opened-modal (modal-key->modal modal-key)]
    [opened-modal e! #(routes/navigate! :home) in-progress?]))

(defn alerts [e! alerts]
  (when (not-empty alerts)
    [:div.container
     (for [alert alerts]
       ^{:key (:timestamp alert)}
       [c/alert (:string alert) #(e! (alerts-controller/->RemoveAlert alert))])]))

(defn page [e! _]
  (routes/start! e!)
  (e! (cc/->RetrieveConfig))
  (fn [e! app]
    [:div
     [navbar/navbar e!]
     [c/progress-bar (:in-progress app)]
     [modal e! (:modal app) (:in-progress app)]
     [alerts e! (:alerts app)]
     [events-page e! app]]))

;; -------------------------
;; Initialize app
(defn mount-components []
  (rdom/render [tuck/tuck state/app page] (.getElementById js/document "app")))

(defn init! []
  (load-interceptors!)
  (session/put! :identity js/identity)
  (mount-components))

(defn ^:dev/after-load  refresh! []
  (init!))
