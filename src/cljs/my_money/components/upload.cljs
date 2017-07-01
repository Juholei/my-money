(ns my-money.components.upload
  (:require [ajax.core :refer [GET POST]]
            [reagent.core :as r]
            [reagent.session :as session]
            [my-money.components.common :as c]
            [my-money.events :as events]))

(defn upload-response-handler [response username]
  (events/get-events @username)
  (session/update! :alerts conj {:string (str "Added " response " events")
                                 :timestamp (.getTime (js/Date.))})
  (session/remove! :modal))

(defn upload [username]
  (let [file-input (.getElementById js/document "file-input")
        file (aget (.-files file-input) 0)
        file-name (.-name file)
        form-data (doto (js/FormData.)
                    (.append "file" file file-name)
                    (.append "username" @username))]
    (POST "/upload" {:body form-data
                     :handler #(upload-response-handler % username)})))

(defn- upload-form [username]
  [:div.form
   [:div.form-group
    [:label "Add your bank csv"
     [:input.form-control-file {:type "file"
                                :id "file-input"}]]]
   [:div.form-group
    [:label "Username"
     [:input {:class "form-control"
              :type "text"
              :value @username
              :on-change #(reset! username (-> % .-target .-value))}]]]])

(defn upload-button [username]
  [:button {:class "btn btn-primary"
            :on-click #(upload username)}
           "Submit"])

(defn upload-modal []
  (let [username (r/atom nil)]
    (fn []
      [c/modal "Upload" [upload-form username] [upload-button username]])))
