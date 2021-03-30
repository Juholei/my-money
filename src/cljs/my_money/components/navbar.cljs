(ns my-money.components.navbar
  (:require [reagent.core :as r]
            [reagent.session :as session]
            [my-money.app.controller.authentication :as ac]
            [my-money.views.login :as login]
            [my-money.views.registration :as registration]
            [my-money.routes :as routes]))

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
      [:a.btn.btn-outline-danger.btn-sm-ml-1
       {:href "#"
        :on-click #(e! (ac/->Logout))}
       [:i.fa.fa-user " " user " | log out"]]]]
    [:ul.navbar-nav.ml-auto
     [:li.nav-item [login/login-button]]
     [:li.nav-item [registration/registration-button]]]))

(defn navbar [e!]
  (let [collapsed? (r/atom true)]
    (fn [e!]
      [:nav.navbar-expand-lg.navbar-dark.mb-2.relative.flex.items-center.px-4.py-2.bg-black.bg-opacity-75
       [:a.text-white.inline-block.whitespace-nowrap.text-lg.mr-4 {:href "#"} "my-money"]
       [:button.navbar-toggler {:type "button"
                                :on-click #(swap! collapsed? not)}
        [:span.navbar-toggler-icon]]
       [:div.collapse.navbar-collapse
        (when-not @collapsed? {:class "show"})
        [:ul.flex.pl-0.mb-0.list-none
         [nav-link "#/" "Home" :home collapsed?]
         (when (session/get :identity)
           [:<>
            [:button.btn.btn-outline-info.fa.fa-cog.fa-inverse.ml-1 {:on-click #(routes/navigate! :config)}]
            [:button.btn.btn-outline-info.ml-1 {:on-click #(routes/navigate! :upload)} "Upload"]])]
        [user-menu e!]]])))
