(ns my-money.views.upload
  (:require [reagent.core :as r]
            [my-money.components.common :as c]
            [my-money.app.controller.upload :as uc]))


(defn upload [e!]
  (let [file-input (.getElementById js/document "file-input")
        file (aget (.-files file-input) 0)
        file-name (.-name file)
        form-data (doto (js/FormData.)
                    (.append "file" file file-name))]
    (e! (uc/->Upload form-data))))

(defn- upload-form []
  [:div.form
   [:div.form-group
    [:label "Add your bank csv"
     [:input.form-control-file {:type "file"
                                :id "file-input"}]]]])

(defn upload-button [e! in-progress?]
  [:button {:class    "btn btn-primary"
            :on-click #(upload e!)
            :disabled in-progress?}
   (if in-progress?
     [c/euro-symbol in-progress?]
     "Submit")])

(defn upload-modal [e! close-fn in-progress?]
  [c/modal "Upload" [upload-form] [upload-button e! in-progress?] close-fn])
