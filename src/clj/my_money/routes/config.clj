(ns my-money.routes.config
  (:require [my-money.db.core :as db]
            [compojure.core :refer [defroutes GET POST]]
            [ring.util.http-response :as response]))

(defn amount-string->cent-integer [string]
  (Integer/parseInt (clojure.string/replace string "." "")))
(defroutes config-routes
  (POST "/save-config" []
    (fn [{:keys [session params]}]
      (let [username (:identity session)
            user-id (:id (db/get-user-by-username {:username username}))
            amount (amount-string->cent-integer (:amount params))]
        (db/update-starting-amount! {:user-id user-id
                                     :starting-amount amount})
        (response/ok))))
  (GET "/get-config" []
     (fn [{:keys [session]}]
       (let [username (:identity session)
             user (db/get-user-by-username {:username username})]
         (response/ok {:starting-amount (:starting_amount user)})))))
