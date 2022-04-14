(ns my-money.routes.public
  (:require [my-money.db.core :as db]
            [compojure.core :refer [defroutes GET context]]
            [clj-time.format :as tf]
            [ring.util.http-response :as response]
            [my-money.config :as config]))

(defroutes public-api
  (context "/public" []
    (GET "/updated" [user]
      (fn [{:keys [headers]}]
        (if (= (get headers "authorization") (-> config/env :secret))
          (response/ok {:lastUpdated (->> (db/get-users-newest-event-date {:username user})
                                           :created
                                           (tf/unparse (:date-time tf/formatters)))})
          (response/forbidden))))))