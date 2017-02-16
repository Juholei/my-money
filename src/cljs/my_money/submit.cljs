(ns my-money.submit
    (:require [reagent.core :as r]
              [ajax.core :refer [GET POST]]))

(def response-data (r/atom nil))

(def username (r/atom nil))

(defn upload-response-handler [response]
    (reset! response-data response))

(defn upload []
  (let [file-input (.getElementById js/document "file-input")
        file (aget (.-files file-input) 0)
        file-name (.-name file)
        form-data (doto (js/FormData.)
                    (.append "file" file file-name)
                    (.append "username" @username))]
    (POST "/upload" {:body form-data
                     :handler upload-response-handler
                     :timeout 100})))

(defn bank-event-table [rows]
    [:div.table-responsive
     [:table.table.table-striped
      [:thead
       [:tr (for [heading (first rows)]
              [:th (str heading)])]]
      [:tbody (for [row (rest rows)]
                [:tr (for [cell row]
                       [:td cell])])]]])

(defn submit-page []
  [:div.container
   [:form#formi {:on-submit upload}
    [:div.form-group
     [:label {:for "file-input"} "Add your bank csv"]
     [:input {:class "form-control-file"
              :id "file-input"
              :type "file"}]]
    [:div.form-inline
     [:label {:for "username"} "Username"]
     [:input {:class "form-control"
              :id "username"
              :type "text"
              :value @username
              :on-change #(reset! username (-> % .-target .-value))}]]
    [:input {:type "submit"
             :value "Submit"}]]
   [bank-event-table @response-data]])
