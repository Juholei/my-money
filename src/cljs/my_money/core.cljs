(ns my-money.core
  (:require [reagent.core :as r]
            [reagent.session :as session]
            [secretary.core :as secretary :include-macros true]
            [goog.events :as events]
            [goog.history.EventType :as HistoryEventType]
            [markdown.core :refer [md->html]]
            [my-money.ajax :refer [load-interceptors!]]
            [my-money.app.controller.authentication :as ac]
            [my-money.app.controller.navigation :as nc]
            [my-money.app.controller.config :as cc]
            [my-money.app.state :as state]
            [my-money.components.common :as c]
            [my-money.views.config :as config]
            [my-money.views.registration :as registration]
            [my-money.views.login :as login]
            [my-money.views.upload :as upload]
            [my-money.views.events :refer [events-page]]
            [tuck.core :as tuck])
  (:import goog.History))

(defn nav-link [uri title page collapsed?]
  [:li.nav-item
   {:class (when (= page (session/get :page)) "active")}
   [:a.nav-link
    {:href uri
     :on-click #(reset! collapsed? true)} title]])

(defn user-menu [e!]
  (if-let [user (session/get :identity)]
    [:ul.navbar-nav.ml-auto
     [:li.nav-item
      [:a.btn.btn-outline-danger.btn-sm
       {:href "#"
        :on-click #(e! (ac/->Logout))}
       [:i.fa.fa-user " " user " | log out"]]]]
    [:ul.navbar-nav.ml-auto
     [:li.nav-item [login/login-button e!]]
     [:li.nav-item [registration/registration-button e!]]]))

(defn navbar [e!]
  (let [collapsed? (r/atom true)]
    (fn [e!]
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
            [:button.btn.btn-outline-info.fa.fa-cog.fa-inverse {:on-click #(e! (nc/->OpenModal :config))}]
            [:button.btn.btn-outline-info {:on-click #(e! (nc/->OpenModal :upload))} "Upload"]])]
        [user-menu e!]]])))

(defn modal-key->modal [key]
  (condp = key
    :registration registration/registration-form
    :login login/login-form
    :config config/config-modal
    :upload upload/upload-modal
    nil))

(defn modal [e! modal-key]
  (when-let [opened-modal (modal-key->modal modal-key)]
    [opened-modal e! #(e! (nc/->CloseModal))]))

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
  (e! (cc/->RetrieveConfig))
  (fn [e! app]
    [:div
     [navbar e!]
     [c/progress-bar (:in-progress app)]
     [modal e! (:modal app)]
     [alerts]
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
