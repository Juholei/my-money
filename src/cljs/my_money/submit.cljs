(ns my-money.submit
    (:require [reagent.core :as r]
              [ajax.core :refer [GET POST]]
              [secretary.core :as secretary]))

(def username (r/atom nil))

(defn upload-response-handler [response]
  (js/console.log (str "response handler" response))
  (secretary/dispatch! "/events"))


(defn upload []
  (let [file-input (.getElementById js/document "file-input")
        file (aget (.-files file-input) 0)
        file-name (.-name file)
        form-data (doto (js/FormData.)
                    (.append "file" file file-name)
                    (.append "username" @username))]
    (POST "/upload" {:body form-data
                     :handler upload-response-handler})))

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
             :value "Submit"}]]])
