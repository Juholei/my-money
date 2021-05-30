(ns my-money.components.common
  (:require [reagent.core :as r :refer-macros [with-let]]
            [my-money.components.button :refer [button]]))

(def close-button-styles ["bg-transparent"
                          "border-0"
                          "appearance-none"
                          "cursor-pointer"
                          "text-xl"
                          "font-bold"
                          "opacity-50"
                          "hover:opacity-75"
                       ;    TODO: restore these when removing bootstrap
                       ;   "py-4"
                       ;   "my-4"
                       ;   "mr-4"
                       ;   "ml-auto"
                          ])
(defn close-button [{:keys [on-click]}]
  [:button {:class close-button-styles
            :on-click on-click}
   "×"])

(defn modal [header body footer close-fn]
  (with-let [_ (-> js/document
                   .-body
                   .-classList
                   (.add "modal-open"))]
    [:div.modal.block
     [:div.modal-dialog.z-50.top-32
      [:div.modal-content
       [:div.modal-header [:span.modal-title.h5 header]
        [close-button {:on-click close-fn}]]
       [:div.modal-body body]
       [:div.modal-footer footer]]]
     [:div.modal-backdrop.z-10.opacity-50 {:on-click close-fn}]]
    (finally (-> js/document
                 .-body
                 .-classList
                 (.remove "modal-open")))))

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
       [:span.input-group-append.input-group-text "✱"]])]])

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
                "fa fa-plus-square"
                "fa fa-minus-square")
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
  (when (> last 1)
    (let [previous-pages (- current 3)
          next-pages (+ current 4)
          first-page-button-to-show (max (if (>= next-pages last)
                                           (- previous-pages (- next-pages last))
                                           previous-pages)
                                         0)
          last-page-button-to-show (min (if (<= previous-pages 0)
                                          (+ next-pages (- 0 previous-pages))
                                          next-pages)
                                        last)]
      [:nav>ul.pagination.justify-content-center
       [page-button false 0 on-click]
       [navigation-button (= current 0) [:span.fa.fa-angle-left] #(on-click (dec current))]
       [more-pages-indicator (not (zero? first-page-button-to-show))]
       (for [i (range first-page-button-to-show last-page-button-to-show)]
         ^{:key (str "paginator-" i)}
         [page-button (= i current) i on-click])
       [more-pages-indicator (not= last-page-button-to-show last)]
       [navigation-button (= (inc current) last) [:span.fa.fa-angle-right] #(on-click (inc current))]
       [page-button false (dec last) on-click]])))

(defn labeled-checkbox [label checked? on-change-fn]
  [:label label
   [:input.mx-2 {:type "checkbox"
                 :checked checked?
                 :on-change #(on-change-fn (-> % .-target .-checked))}]])

(defn euro-symbol [rotating?]
  (let [state (r/atom {:rotation 0})]
    (fn [rotating?]
      (when rotating?
        (js/setTimeout #(swap! state update :rotation (fn [x] (+ 5 x))) 15))
      [:svg {:width  "16"
             :height "16"}
       [:g {:transform (str "scale(0.04) translate(70, 80) rotate("
                            (if rotating?
                              (:rotation @state)
                              0)
                            ", 112.5, 150)")}
        [:path {:stroke "none"
                :fill   "white"
                :d      "m 224.99996,16.22698 -8.11342,36.41161 q -24.14255,-19.78892 -54.61741,-19.78892 -41.3588,0 -65.00658,23.74671 -23.647774,23.7467 -28.397115,53.23215 l 134.960355,0 -5.14505,26.71508 -132.981532,0 -0.395848,7.71771 0.395848,18.20566 127.242642,0 -5.14505,26.71508 -117.941954,0 q 7.519719,40.17154 32.552754,59.06997 25.03303,18.89844 56.49745,18.89844 37.20302,0 57.98149,-19.59107 l 0,40.9631 Q 192.34828,300 162.26913,300 53.034301,300 30.474864,189.18206 l -30.474864,0 5.738751,-26.71508 20.580475,0 q -0.395708,-4.74934 -0.395708,-17.80995 l 0,-8.11342 -25.923518,0 5.738751,-26.71508 23.152999,0 Q 39.181988,55.21112 76.583149,27.60556 113.98417,0 163.06069,0 199.868,0 224.99996,16.22698"}]]])))

(defn disableable-button
  "Button that shows different content based on whether it is enabled or disabled
   but stays the same size no matter which state it is in."
  [enabled-content disabled-content disabled? on-click-fn]
  [button {:type :primary
           :on-click #(on-click-fn)
           :disabled disabled?
           :style {:position :relative}}
   [:<>
    (when disabled?
      [:span {:style {:position :absolute
                      :margin-left :auto
                      :margin-right :auto
                      :left 0
                      :right 0}}
       disabled-content])
    [:span (when disabled? {:style {:visibility :hidden}}) enabled-content]]])

(defn sticky-bottom-container [contents]
  [:div.sticky.flex.justify-center.w-full.bottom-0
   contents])
