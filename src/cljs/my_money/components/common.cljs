(ns my-money.components.common
  (:require [reagent.core :as r]))

(defn modal [header body footer close-fn]
  [:div
   [:div.modal-dialog
    [:div.modal-content
     [:div.modal-header [:span.modal-title.h5 header]
                        [:button.close {:type "button"
                                        :on-click close-fn}
                                       "×"]]
     [:div.modal-body body]
     [:div.modal-footer footer]]]
   [:div.modal-backdrop {:on-click close-fn}]])

(defn alert [message close-fn]
  [:div.alert.alert-success.alert-dismissible
   [:button.close {:type "button"
                   :on-click #(close-fn)}
    "×"]
   message])

(defn input [type id placeholder fields]
  [:input.form-control.input-lg {:type type
                                 :placeholder placeholder
                                 :value (id @fields)
                                 :on-change #(swap! fields assoc id (-> % .-target .-value))}])

(defn form-input [type label id placeholder fields optional?]
  [:div.form-group
   [:label label
    (if optional?
      [input type id placeholder fields]
      [:div.input-group
       [input type id placeholder fields]
       [:span.input-group-addon "✱"]])]])

(defn text-input [label id placeholder fields & [optional?]]
  (form-input :text label id placeholder fields optional?))

(defn number-input [label id placeholder fields & [optional?]]
  (form-input :number label id placeholder fields optional?))

(defn search-input [label id placeholder fields & [optional?]]
  (form-input :search label id placeholder fields optional?))

(defn password-input [label id placeholder fields & [optional?]]
  (form-input :password label id placeholder fields optional?))

(defn collapsing-button [collapsed?]
  [:i {:class (if @collapsed?
                "fa fa-plus-square-o"
                "fa fa-minus-square-o")
       :on-click #(swap! collapsed? not)}])

(defn progress-bar [visible]
  (when visible
    [:div.progress-bar.progress-bar-striped.progress-bar-animated {:style {:width "100%"
                                                                           :height "0.3em"
                                                                           :margin-top "-10px"}}]))

(defn- page-button [active? index on-click]
  [:li.page-item {:class (when active? "active")}
   [:a.page-link {:href "#"
                  :on-click #(do (.preventDefault %)
                                 (on-click index))}
                 (inc index)]])

(defn- more-pages-indicator [visible?]
  (when visible?
    [:li.page-item.disabled>a.page-link {:href "#"} "..."]))

(defn- navigation-button [disabled? text on-click-fn]
  [:li.page-item {:class (when disabled? "disabled")}
   [:a.page-link {:href "#"
                  :on-click #(do (.preventDefault %)
                                 (on-click-fn))}
    text]])

(defn paginator
  "on-click parameter is a function that takes as a parameter
   the index of the page to navigate to"
  [current last on-click]
  (let [first-page-button-to-show (max (- current 3) 0)
        last-page-button-to-show (min (+ current 3) last)]
    [:nav>ul.pagination.justify-content-center
     [page-button false 0 on-click]
     [navigation-button (= current 0) "Previous" #(on-click (dec current))]
     [more-pages-indicator (not (zero? first-page-button-to-show))]
     (for [i (range first-page-button-to-show last-page-button-to-show)]
       ^{:key (str "paginator-" i)}
       [page-button (= i current) i on-click])
     [more-pages-indicator (not= last-page-button-to-show last)]
     [navigation-button (= (inc current) last) "Next" #(on-click (inc current))]
     [page-button false (dec last) on-click]]))
