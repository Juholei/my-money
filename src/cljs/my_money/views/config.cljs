(ns my-money.views.config
  (:require [clojure.string :as string]
            [my-money.app.state :as state]
            [my-money.app.controller.config :as cc]
            [my-money.app.controller.navigation :as nc]
            [my-money.components.common :as c]
            [reagent.core :as r]
            [my-money.utils :as utils]))

(def tabs [{:name "Savings" :id :savings} {:name "Event types" :id :types}])

(defn search [search-term strings]
  (filter #(string/includes?
            (string/lower-case %)
            (string/lower-case search-term)) strings))

(defn toggle [a-set elem]
  (if (contains? a-set elem)
    (disj a-set elem)
    (conj a-set elem)))

(defn result-list-item [data key content]
  [:li.list-group-item
   {:on-click #(swap! data update key toggle content)
    :class (when (some #{content} (key @data))
             "active")}
   content])

(defn search-result-list [data recipients]
  (when (< 0 (count (:recipient-search @data)))
    (let [recipient (:recipient-search @data)
          matched-recipients (search recipient recipients)]
      (into [:ul.list-group]
        (for [matched-recipient matched-recipients]
          ^{:key matched-recipient}
          [result-list-item data :recipients matched-recipient])))))

(defn savings-recipient-list [data]
  [:div
   [:h6 "Savings recipients"]
   (into [:ul.list-group]
     (for [recipient (:recipients @data)]
       ^{:key (str "active_" recipient)}
       [:li.list-group-item recipient
        [:button.btn.close
         {:on-click #(swap! data update :recipients disj recipient)}
         "×"]]))])


(defn- fields [data tabs]
  [:div
   tabs
   [c/number-input "Starting amount (Amount of money before first event)" :starting-amount "Amount" data [false]]
   [c/search-input "Add recipients as savings" :recipient-search "Recipient" data [false]]
   [search-result-list data (into #{} (map :recipient (:events @state/app)))]
   [savings-recipient-list data]])

(defn- buttons [e! data in-progress? close-fn]
  [:div
   [c/disableable-button "Save" [c/euro-symbol in-progress?] in-progress? #(e! (cc/->SaveConfig @data))]
   [:button.btn.btn-danger {:on-click #(close-fn)} "Cancel"]])

(defn savings-config [e! fields-data tabs in-progress? close-fn]
  [c/modal "Configuration"
   [fields fields-data tabs]
   [buttons e! fields-data in-progress? close-fn]
   close-fn])

(defn type-config-form [tabs types]
  (let [types-to-recipients (reduce (fn [acc [key val]] (update acc val conj key)) {} types)]
    [:div
     tabs
     [:p "Here you can configure event type texts of your bank events"]
     [:table.table.table-striped
      [:thead
       [:tr
        [:th "Key"]
        [:th "Recipient"]
        [:th "Label"]]]
      [:tbody
       (for [[key recipients] types-to-recipients]
         ^{:key (str recipients key)}
         [:tr
          [:td key]
          [:td
           [:ul (for [recipient recipients]
                  ^{:key recipient}
                  [:li recipient])]]
          [:td [:input {:type "text"}]]])]]]))

(defn type-config [e! fields-data tabs in-progress? close-fn types]
  [c/modal "Configuration"
   [type-config-form tabs types]
   [buttons e! fields-data in-progress? close-fn]
   close-fn])

(defn config-modal [e! close-fn in-progress? section]
  (let [fields-data (r/atom {:starting-amount (/ (:starting-amount @state/app) 100)
                             :recipient-search ""
                             :recipients (:recipients @state/app)})
        tab-bar (fn [section] [c/tab-bar tabs section #(e! (nc/->SelectConfigSection %))])]
    (fn [e! close-fn in-progress? section]
      (condp = section
        :savings [savings-config e! fields-data [tab-bar section] in-progress? close-fn]
        :types [type-config e! fields-data [tab-bar section] in-progress? close-fn (utils/events->recipient-types (:events @state/app))]))))
