(ns my-money.views.config
  (:require [clojure.string :as string]
            [my-money.app.state :as state]
            [my-money.components.button :refer [button]]
            [my-money.app.controller.config :as cc]
            [my-money.components.common :as c]
            [reagent.core :as r]))

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
   (into [:ul.list-group.]
     (for [recipient (:recipients @data)]
       ^{:key (str "active_" recipient)}
       [:li.block.relative.py-3.px-5.-mb-px.bg-white.border-solid.border.border-black.first:rounded
        recipient
        [:button.btn.close.ml-1
         {:on-click #(swap! data update :recipients disj recipient)}
         "×"]]))])


(defn- fields [data]
  [:div
   [c/number-input "Starting amount (Amount of money before first event)" :starting-amount "Amount" data [false]]
   [c/search-input "Add recipients as savings" :recipient-search "Recipient" data [false]]
   [search-result-list data (into #{} (map :recipient (:events @state/app)))]
   [savings-recipient-list data]])

(defn- buttons [e! data in-progress? close-fn]
  [:div
   [c/disableable-button "Save" [c/euro-symbol in-progress?] in-progress? #(e! (cc/->SaveConfig @data))]
   [button {:type :danger
            :on-click close-fn}
    "Cancel"]])

(defn config-modal [e! close-fn in-progress?]
  (r/with-let [fields-data (r/atom {:starting-amount (/ (:starting-amount @state/app) 100)
                                    :recipient-search ""
                                    :recipients (:recipients @state/app)})]
    [c/modal "Configuration"
     [fields fields-data]
     [buttons e! fields-data in-progress? close-fn]
     close-fn]))
