(ns my-money.app.controller.upload
  (:require [my-money.ajax :as ajax]
            [my-money.app.controller.alerts :as ac]
            [my-money.app.controller.common :as c]
            [my-money.app.controller.events :as ec]
            [my-money.app.controller.navigation :as nc]
            [my-money.routes :as routes]
            [tuck.core :as tuck]))

(defrecord Upload [form-data])
(defrecord UploadFinished [response])


(extend-protocol tuck/Event
  Upload
  (process-event [{form-data :form-data} app]
    (tuck/fx (nc/set-in-progress app true)
             {:tuck.effect/type ::ajax/post
              :url              "/upload"
              :body             form-data
              :on-success       ->UploadFinished
              :on-error         c/->ErrorHandler}))

  UploadFinished
  (process-event [{response :response} app]
    (tuck/action! (fn [e!]
                    (e! (ec/->RetrieveEvents))
                    (e! (ec/->RetrieveRecurringExpenses))
                    (routes/navigate! :home)))
    (-> app
        (nc/set-in-progress false)
        (dissoc :modal)
        (ac/add-alert (str "Added " response " events")
                      (.getTime (js/Date.))))))
