(ns my-money.routes.config
  (:require [my-money.db.core :as db]
            [compojure.core :refer [defroutes GET POST]]
            [ring.util.http-response :as response]))

(defn amount-string->cent-integer [amount]
  (if (clojure.string/includes? amount ".")
    (Integer/parseInt (clojure.string/replace amount "." ""))
    (* 100 (Integer/parseInt amount))))

(defn to-db-array [db v]
  (.createArrayOf (.getConnection (:datasource db)) "text" (into-array v)))

(defroutes config-routes
  (POST "/save-config" []
    (fn [{:keys [session params]}]
      (let [username (:identity session)
            user-id (:id (db/get-user-by-username {:username username}))]
        (when (:starting-amount params)
          (let [amount (amount-string->cent-integer (:starting-amount params))]
            (db/update-starting-amount! {:user-id user-id
                                         :starting-amount amount})))
        (when-let [recipients (:recipients params)]
          (db/save-savings! {:user-id user-id
                             :recipients (to-db-array db/*db* (vec recipients))})))
      (response/ok)))

  (GET "/get-config" []
     (fn [{:keys [session]}]
       (let [username (:identity session)
             user (db/get-user-by-username {:username username})
             savings-recipients (:recipients (db/get-savings {:user-id (:id user)}))]
         (response/ok {:starting-amount (str (:starting_amount user))
                       :recipients (set savings-recipients)})))))
