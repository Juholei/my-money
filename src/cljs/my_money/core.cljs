(ns my-money.core
  (:require [reagent.core :as r]
            [reagent.session :as session]
            [secretary.core :as secretary :include-macros true]
            [goog.events :as events]
            [goog.history.EventType :as HistoryEventType]
            [markdown.core :refer [md->html]]
            [my-money.ajax :refer [load-interceptors!]]
            [my-money.app.controller.events :as ec]
            [my-money.app.controller.config :as cc]
            [my-money.app.state :as state]
            [my-money.components.common :as c]
            [my-money.components.config :as config]
            [my-money.components.registration :as registration]
            [my-money.components.login :as login]
            [my-money.components.upload :as upload]
            [my-money.events :refer [events-page]]
            [ajax.core :refer [GET POST]])
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
        :on-click #(POST "/logout"
                         {:handler (fn []
                                     (session/remove! :identity))})}
       [:i.fa.fa-user " " user " | log out"]]]]
    [:ul.navbar-nav.ml-auto
     [:li.nav-item [login/login-button]]
     [:li.nav-item [registration/registration-button]]]))

(defn navbar []
  (let [collapsed? (r/atom true)]
    (fn []
      [:nav.navbar.navbar-expand-lg.navbar-dark.bg-dark
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

(defn about-page []
  [:div.container
   [:div.row
    [:div.col-md-12
     "this is the story of my-money... work in progress"]]])

(defn home-page []
  [:div.container
   [events-page @state/app]])

(def pages
  {:home #'home-page
   :about #'about-page})

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

(defn page []
  [:div
   [modal]
   [alerts]
   [(pages (session/get :page))]])

;; -------------------------
;; Routes
(secretary/set-config! :prefix "#")

(secretary/defroute "/" []
  (session/put! :page :home))

(secretary/defroute "/about" []
  (session/put! :page :about))

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
(defn fetch-docs! []
  (GET "/docs" {:handler #(session/put! :docs %)}))

(defn mount-components []
  (r/render [#'navbar] (.getElementById js/document "navbar"))
  (r/render [#'page] (.getElementById js/document "app")))

(defn init! []
  (load-interceptors!)
  (fetch-docs!)
  (hook-browser-navigation!)
  (session/put! :identity js/identity)
  (cc/get-config)
  (ec/get-events)
  (mount-components))
