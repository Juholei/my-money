(ns my-money.components.upload
  (:require [reagent.core :as r]
            [my-money.components.common :as c]
            [my-money.app.controller.upload :as uc]))


(defn upload []
  (let [file-input (.getElementById js/document "file-input")
        file (aget (.-files file-input) 0)
        file-name (.-name file)
        form-data (doto (js/FormData.)
                    (.append "file" file file-name))]
    (uc/upload! form-data)))

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
