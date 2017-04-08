(ns my-money.submit
    (:require [reagent.core :as r]
              [ajax.core :refer [GET POST]]
              [secretary.core :as secretary]
              [my-money.events :refer [get-events]]))

(defonce username (r/atom nil))
(defonce alerts (r/atom []))

(defn upload-response-handler [response]
  (js/console.log (str "response handler" response))
  (swap! alerts conj (str "Added " response " events"))
  (get-events @username))

(defn upload []
  (let [file-input (.getElementById js/document "file-input")
        file (aget (.-files file-input) 0)
        file-name (.-name file)
        form-data (doto (js/FormData.)
                    (.append "file" file file-name)
                    (.append "username" @username))]
    (POST "/upload" {:body form-data
                     :handler upload-response-handler})))

(defn alerts-display [alerts]
  [:div.container
  (for [alert alerts]
    [:div {:class "alert alert-success"} alert])])

(defn submit-page []
  [:div.container
   [alerts-display @alerts]
   [:form.form-inline {:on-submit upload}
    [:div.form-group
     [:label {:for "file-input"} "Add your bank csv"]
     [:input {:id "file-input"
              :type "file"}]]
    [:div.form-group
     [:label {:for "username"} "Username"]
     [:input {:class "form-control"
              :id "username"
              :type "text"
              :value @username
              :on-change #(reset! username (-> % .-target .-value))}]]
    [:input {:class "btn btn-primary"
             :type "submit"
             :value "Submit"}]]])
