(ns my-money.app.controller.upload
  (:require [ajax.core :as ajax]
            [my-money.app.controller.alerts :as ac]
            [my-money.app.controller.events :as ec]
            [my-money.app.controller.navigation :as nc]
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
                    (e! (ec/->RetrieveEvents))
                    (e! (ec/->RetrieveRecurringExpenses))))
    (-> app
        (nc/set-in-progress false)
        (dissoc :modal)
        (ac/add-alert (str "Added " response " events")
                      (.getTime (js/Date.))))))
