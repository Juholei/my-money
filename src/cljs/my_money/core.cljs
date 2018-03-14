(ns my-money.core
  (:require [reagent.core :as r]
            [reagent.session :as session]
            [secretary.core :as secretary :include-macros true]
            [goog.events :as events]
            [goog.history.EventType :as HistoryEventType]
            [my-money.ajax :refer [load-interceptors!]]
            [my-money.app.controller.alerts :as alerts-controller]
            [my-money.app.controller.navigation :as nc]
            [my-money.app.controller.config :as cc]
            [my-money.app.state :as state]
            [my-money.components.common :as c]
            [my-money.components.navbar :as navbar]
            [my-money.views.config :as config]
            [my-money.views.registration :as registration]
            [my-money.views.login :as login]
            [my-money.views.upload :as upload]
            [my-money.views.events :refer [events-page]]
            [tuck.core :as tuck])
  (:import goog.History))

(defn modal-key->modal [key]
  (condp = key
    :registration registration/registration-form
    :login login/login-form
    :config config/config-modal
    :upload upload/upload-modal
    nil))

(defn modal [e! modal-key in-progress?]
  (when-let [opened-modal (modal-key->modal modal-key)]
    [opened-modal e! #(e! (nc/->CloseModal)) in-progress?]))

(defn alerts [e! alerts]
  (when (not-empty alerts)
    [:div.container
     (for [alert alerts]
       ^{:key (:timestamp alert)}
       [c/alert (:string alert) #(e! (alerts-controller/->RemoveAlert alert))])]))

(defn page [e! app]
  (e! (cc/->RetrieveConfig))
  (when-not (session/get :identity)
    (e! (nc/->OpenModal :login)))
  (fn [e! app]
    [:div
     [navbar/navbar e!]
     [c/progress-bar (:in-progress app)]
     [modal e! (:modal app) (:in-progress app)]
     [alerts e! (:alerts app)]
     [events-page e! app]]))

;; -------------------------
;; Routes
(secretary/set-config! :prefix "#")

(secretary/defroute "/" []
  (session/put! :page :home))

;; -------------------------
;; History
;; must be called after routes have been defined
(defn hook-browser-navigation! []
  (doto (History.)
        (events/listen
          HistoryEventType/NAVIGATE
          (fn [event]
              (secretary/dispatch! (.-token event))))
        (.setEnabled true)))

;; -------------------------
;; Initialize app
(defn mount-components []
  (r/render (tuck/tuck state/app page) (.getElementById js/document "app")))

(defn init! []
  (load-interceptors!)
  (hook-browser-navigation!)
  (session/put! :identity js/identity)
  (mount-components))
