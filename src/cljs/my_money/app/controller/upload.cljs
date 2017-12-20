(ns my-money.app.controller.upload
  (:require [ajax.core :as ajax]
            [my-money.app.controller.events :as ec]
            [my-money.app.controller.navigation :as nc]
            [reagent.session :as session]
            [tuck.core :as tuck]))

(defrecord Upload [form-data])
(defrecord UploadFinished [response])

(extend-protocol tuck/Event
  Upload
  (process-event [{form-data :form-data} app]
    (tuck/action! (fn [e!]
                   (ajax/POST "/upload" {:body form-data
                                         :handler #(e! (->UploadFinished %))})))
    (nc/set-in-progress app true))

  UploadFinished
  (process-event [{response :response} app]
    (tuck/action! (fn [e!]
                    (e! (ec/->RetrieveEvents))))
    (session/update! :alerts conj {:string    (str "Added " response " events")
                                   :timestamp (.getTime (js/Date.))})
    (session/remove! :modal)
    (nc/set-in-progress app false)))