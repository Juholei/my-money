(ns my-money.components.navbar
  (:require [reagent.core :as r]
            [reagent.session :as session]
            [my-money.app.controller.authentication :as ac]
            [my-money.views.login :as login]
            [my-money.views.registration :as registration]
            [my-money.routes :as routes]))

(defn nav-item [child]
  [:li.nav-item
   child])

(defn user-menu [e!]
  [:ul.flex.pl-0.mb-0.list-none.ml-auto
   (if-let [user (session/get :identity)]
     [:<>
      [:li.nav-item
       [:a.btn.btn-outline-danger.btn-sm-ml-1
        {:href "#"
         :on-click #(e! (ac/->Logout))}
        [:i.fa.fa-user " " user " | log out"]]]]
     [:<>
      [nav-item [login/login-button]]
      [nav-item [registration/registration-button]]])])

(defn navbar [e!]
  (r/with-let [collapsed? (r/atom true)]
    [:nav.navbar-expand-lg.navbar-dark.mb-2.relative.flex.items-center.px-4.py-2.bg-black.bg-opacity-75
     [:a.text-white.inline-block.whitespace-nowrap.text-lg.mr-4 {:href "#"} "my-money"]
     [:button.navbar-toggler {:type "button"
                              :on-click #(swap! collapsed? not)}
      [:span.navbar-toggler-icon]]
     [:div.collapse.navbar-collapse
      (when-not @collapsed? {:class "show"})
      [:ul.flex.pl-0.mb-0.list-none
       (when (session/get :identity)
         [:<>
          [:button.btn.btn-outline-info.fa.fa-cog.fa-inverse.ml-1 {:on-click #(routes/navigate! :config)}]
          [:button.btn.btn-outline-info.ml-1 {:on-click #(routes/navigate! :upload)} "Upload"]])]
      [user-menu e!]]]))
