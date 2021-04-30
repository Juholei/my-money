(ns my-money.components.navbar
  (:require [reagent.core :as r]
            [reagent.session :as session]
            [my-money.app.controller.authentication :as ac]
            [my-money.routes :as routes]))

(defn registration-button []
  [:a.block.py-2.px-4 {:href "#/register"} "Register"])

(defn login-button []
  [:a.block.py-2.px-4 {:href "#/login"} "Login"])

(def logout-button-styles ["inline-block"
                           "font-normal"
                           "text-center"
                           "align-middle"
                           "select-none"
                           "bg-transparent"
                           "border-2" "border-solid"
                           "text-base"
                           "py-1.5"
                           "px-3"
                           "rounded"
                           "border-red-danger"
                           "text-red-danger"
                           "hover:text-white"])

(defn logout-button [e! user]
  [:a
   ; TODO .btn transitions need to be replaced
   {:class logout-button-styles
    :href "#"
    :on-click #(e! (ac/->Logout))}
   [:i.fa.fa-user " " user " | log out"]])

(defn nav-item [child]
  [:li
   child])

(defn user-menu [e!]
  [:ul.flex.pl-0.mb-0.list-none.ml-auto
   (if-let [user (session/get :identity)]
     [:<>
      [nav-item
       [logout-button e! user]]]
     [:<>
      [nav-item [login-button]]
      [nav-item [registration-button]]])])

(defn navbar [e!]
  (r/with-let [collapsed? (r/atom true)]
    [:nav.mb-2.relative.flex.md:flex-row.md:flex-nowrap.md:justify-start.items-center.px-4.py-2.bg-black.bg-opacity-75
     [:a.text-white.inline-block.whitespace-nowrap.text-lg.mr-4 {:href "#"} "my-money"]
     #_[:button.navbar-toggler {:type "button"
                              :on-click #(swap! collapsed? not)}
      [:span.navbar-toggler-icon]]
     ;; TODO: Reimplement .collapse class
     [:div.flex.flex-auto
      (when-not @collapsed? {:class "show"})
      [:ul.flex.pl-0.mb-0.list-none
       (when (session/get :identity)
         [:<>
          [:button.btn.btn-outline-info.fa.fa-cog.fa-inverse.ml-1 {:on-click #(routes/navigate! :config)}]
          [:button.btn.btn-outline-info.ml-1 {:on-click #(routes/navigate! :upload)} "Upload"]])]
      [user-menu e!]]]))
