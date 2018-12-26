(ns my-money.ajax
  (:require [ajax.core :as ajax]
            [tuck.effect :as fx]))

(defn local-uri? [{:keys [uri]}]
  (not (re-find #"^\w+?://" uri)))

(defn default-headers [request]
  (if (local-uri? request)
    (-> request
        (update :uri #(str js/context %))
        (update :headers #(merge {"x-csrf-token" js/csrfToken} %)))
    request))

(defn load-interceptors! []
  (swap! ajax/default-interceptors
         conj
         (ajax/to-interceptor {:name "default headers"
                               :request default-headers})))


(defmethod fx/process-effect ::get [e! {:keys [url on-success on-error]}]
  (ajax/GET url
            {:handler       #(e! (on-success %))
             :error-handler #(e! (on-error %))}))

(defmethod fx/process-effect ::post [e! {:keys [url headers params on-success on-error]}]
  (ajax/POST url
             (cond-> {:params        params
                      :handler       #(e! (on-success %))
                      :error-handler #(e! (on-error %))}
                     headers (assoc :headers headers))))
