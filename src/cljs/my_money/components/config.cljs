(ns my-money.components.config
  (:require [clojure.string :as string]
            [my-money.components.common :as c]
            [ajax.core :as ajax]
            [reagent.core :as r]
            [reagent.session :as session]))

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
          [result-list-item data :selected-recipients matched-recipient])))))

(defn savings-recipient-list [data]
  [:div
   [:h6 "Savings recipients"]
   (into [:ul.list-group]
     (for [recipient (:selected-recipients @data)]
       ^{:key (str "active_" recipient)}
       [:li.list-group-item recipient
        [:button.btn.close
         {:on-click #(swap! data update :selected-recipients disj recipient)}
         "×"]]))])

(defn- fields [data]
  [:div
   [c/number-input "Starting amount (Amount of money before first event)" :amount "Amount" data [false]]
   [c/search-input "Add recipients as savings" :recipient-search "Recipient" data [false]]
   [search-result-list data ["Savings account" "Fund"]]
   [savings-recipient-list data]])

(defn config-saved []
  (session/remove! :modal))

(defn save-config! [config]
  (ajax/POST "/save-config"
             {:params @config
              :handler config-saved}))

(defn- buttons [data]
  [:div
   [:button.btn.btn-primary {:on-click #(save-config! data)} "Save"]
   [:button.btn.btn-danger {:on-click #(c/close-modal)} "Cancel"]])

(defn config-modal []
  (let [fields-data (r/atom {:selected-recipients #{}})]
    (fn []
      [c/modal "Configuration"
               [fields fields-data]
               [buttons fields-data]])))
