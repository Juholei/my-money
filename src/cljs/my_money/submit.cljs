(ns my-money.submit
    (:require [reagent.core :as r]
              [ajax.core :refer [GET POST]]))

(def response-data (r/atom nil))

(defn upload-response-handler [response]
    (reset! response-data response))

(defn upload []
  (let [file-input (.getElementById js/document "file-input")
        file (aget (.-files file-input) 0)
        file-name (.-name file)
        form-data (doto (js/FormData.) (.append "file" file file-name))]
    (POST "/upload" {:body form-data
                     :handler upload-response-handler
                     :timeout 100})))

(defn bank-event-table [rows]
    [:div.container
     [:table
      [:thead
       [:tr (for [heading (first rows)]
              [:th (str heading)])]]]])

(defn submit-page []
  [:div.container
   [:form#formi {:on-submit upload}
    [:div.form-group
     [:label {:for "file-input"} "Add your bank csv"]
     [:input {:class "form-control-file"
              :id "file-input"
              :type "file"}]]
     [:input {:type "submit"
              :value "Submit"}]]
   [bank-event-table @response-data]])
