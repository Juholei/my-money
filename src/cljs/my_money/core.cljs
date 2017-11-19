(ns my-money.core
  (:require [reagent.core :as r]
            [reagent.session :as session]
            [secretary.core :as secretary :include-macros true]
            [goog.events :as events]
            [goog.history.EventType :as HistoryEventType]
            [markdown.core :refer [md->html]]
            [my-money.ajax :refer [load-interceptors!]]
            [my-money.app.controller.authentication :as ac]
            [my-money.app.controller.events :as ec]
            [my-money.app.controller.config :as cc]
            [my-money.app.state :as state]
            [my-money.components.common :as c]
            [my-money.components.config :as config]
            [my-money.components.registration :as registration]
            [my-money.components.login :as login]
            [my-money.components.upload :as upload]
            [my-money.events :refer [events-page]]
            [tuck.core :as tuck])
  (:import goog.History))

(defn nav-link [uri title page collapsed?]
  [:li.nav-item
   {:class (when (= page (session/get :page)) "active")}
   [:a.nav-link
    {:href uri
     :on-click #(reset! collapsed? true)} title]])

(defn user-menu []
  (if-let [user (session/get :identity)]
    [:ul.navbar-nav.ml-auto
     [:li.nav-item
      [:a.btn.btn-outline-danger.btn-sm
       {:href "#"
        :on-click #(ac/logout!)}
       [:i.fa.fa-user " " user " | log out"]]]]
    [:ul.navbar-nav.ml-auto
     [:li.nav-item [login/login-button]]
     [:li.nav-item [registration/registration-button]]]))

(defn navbar []
  (let [collapsed? (r/atom true)]
    (fn []
      [:nav#navbar.navbar.navbar-expand-lg.navbar-dark.bg-dark
       [:a.navbar-brand {:href "#"} "my-money"]
       [:button.navbar-toggler {:type "button"
                                :on-click #(swap! collapsed? not)}
        [:span.navbar-toggler-icon]]
       [:div.collapse.navbar-collapse
        (when-not @collapsed? {:class "show"})
        [:ul.navbar-nav
         [nav-link "#/" "Home" :home collapsed?]
         (when (session/get :identity)
           [:ul.navbar-nav
            [:button.btn.btn-outline-info.fa.fa-cog.fa-inverse {:on-click #(session/put! :modal config/config-modal)}]
            [:button.btn.btn-outline-info {:on-click #(session/put! :modal upload/upload-modal)} "Upload"]])]
        [user-menu]]])))

(defn modal []
  (when-let [session-modal (session/get :modal)]
    [session-modal]))

(defn remove-alert [alert]
  (fn []
    (let [correct-alert? (fn [another-alert]
                           (= (:timestamp alert) (:timestamp another-alert)))]
      (session/update! :alerts (partial remove correct-alert?)))))

(defn alerts []
  (when-let [session-alerts (session/get :alerts)]
    [:div.container
     (for [alert session-alerts]
       ^{:key (:timestamp alert)}
       [c/alert (:string alert) (remove-alert alert)])]))

(defn page [e! app]
  [:div
   [navbar]
   [modal]
   [alerts]
   [events-page e! app]])

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
  (cc/get-config)
  (ec/get-events)
  (mount-components))
