(ns my-money.components.upload
  (:require [ajax.core :refer [GET POST]]
            [reagent.core :as r]
            [reagent.session :as session]
            [my-money.components.common :as c]
            [my-money.app.controller.events :as ec]))

(defn upload-response-handler [response]
  (ec/get-events)
  (session/update! :alerts conj {:string (str "Added " response " events")
                                 :timestamp (.getTime (js/Date.))})
  (session/remove! :modal))

(defn upload []
  (let [file-input (.getElementById js/document "file-input")
        file (aget (.-files file-input) 0)
        file-name (.-name file)
        form-data (doto (js/FormData.)
                    (.append "file" file file-name))]
    (POST "/upload" {:body form-data
                     :handler upload-response-handler})))

(defn- upload-form []
  [:div.form
   [:div.form-group
    [:label "Add your bank csv"
     [:input.form-control-file {:type "file"
                                :id "file-input"}]]]])

(defn upload-button []
  [:button {:class "btn btn-primary"
            :on-click #(upload)}
           "Submit"])

(defn upload-modal []
  [c/modal "Upload" [upload-form] [upload-button]])
