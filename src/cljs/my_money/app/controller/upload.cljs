(ns my-money.app.controller.upload
  (:require [ajax.core :as ajax]
            [my-money.app.controller.events :as ec]
            [reagent.session :as session]))

(defn- upload-response-handler [e! response]
  (e! (ec/->GetEvents))
  (session/update! :alerts conj {:string    (str "Added " response " events")
                                 :timestamp (.getTime (js/Date.))})
  (session/remove! :modal))

(defn upload! [e! form-data]
  (ajax/POST "/upload" {:body form-data
                        :handler #(upload-response-handler e! %)}))
