(ns my-money.test.db.core
  (:require [my-money.db.core :refer [*db*] :as db]
            [luminus-migrations.core :as migrations]
            [clojure.test :refer :all]
            [clojure.java.jdbc :as jdbc]
            [clj-time.core :as t]
            [clj-time.coerce :as c]
            [my-money.config :refer [env]]
            [my-money.routes.config :refer [to-db-array]]
            [mount.core :as mount]))

(use-fixtures
  :once
  (fn [f]
    (mount/start
     #'my-money.config/env
     #'my-money.db.core/*db*)
    (migrations/migrate ["migrate"] (select-keys env [:database-url]))
    (f)))

(def test-event {:transaction-id "2412231/876567/9412363"
                 :transaction-date (c/to-date (t/date-time 2017 02 24))
                 :amount -500
                 :recipient "Test recipient"
                 :type "123"})

(def user {:username "test_person"
            :password "testpassword"})

(deftest test-users
  (jdbc/with-db-transaction [t-conn *db*]
    (jdbc/db-set-rollback-only! t-conn)
    (is (= 1 (db/create-user!
              t-conn
              user)))
    (is (= "test_person" (:username (db/get-user-by-username t-conn {:username "test_person"}))))))

(deftest test-events
  (jdbc/with-db-transaction [t-conn *db*]
    (jdbc/db-set-rollback-only! t-conn)
    (db/create-user!
     t-conn
     user)
    (let [user-id (:id (db/get-user-by-username t-conn {:username "test_person"}))]
      (is (= 1 (db/create-event! t-conn (assoc test-event :user-id user-id))))
      (is (= 1 (count (db/get-events t-conn {:user-id user-id})))))))

(deftest test-duplicate-events-are-not-added
  (jdbc/with-db-transaction [t-conn *db*]
    (jdbc/db-set-rollback-only! t-conn)
    (db/create-user!
     t-conn
     user)
    (let [user-id (:id (db/get-user-by-username t-conn {:username "test_person"}))]
      (is (= 1 (db/create-event! t-conn (assoc test-event :user-id user-id))))
      (is (= 0 (db/create-event! t-conn (assoc test-event :user-id user-id)))))))

(deftest test-recurring-events-only-for-correct-user
  (jdbc/with-db-transaction [t-conn *db*]
    (jdbc/db-set-rollback-only! t-conn)
    (db/create-user!
     t-conn
     user)
    (db/create-user!
     t-conn
     {:username "test_person2"
      :password "testpassword"})
    (let [user-id (:id (db/get-user-by-username t-conn {:username "test_person"}))
          user-id2 (:id (db/get-user-by-username t-conn {:username "test_person2"}))]
      (is (= 1 (db/create-event! t-conn (assoc test-event :user-id user-id))))
      (is (= 1 (db/create-event! t-conn (assoc test-event :user-id user-id2))))
      (is (empty? (db/get-recurring-expenses t-conn {:user-id user-id}))))))


(deftest test-recurring-events-are-found
  (jdbc/with-db-transaction [t-conn *db*]
    (jdbc/db-set-rollback-only! t-conn)
    (db/create-user!
     t-conn
     user)
    (let [user-id (:id (db/get-user-by-username t-conn {:username "test_person"}))
          test-event-for-user (assoc test-event :user-id user-id)]
      (is (= 1 (db/create-event! t-conn test-event-for-user)))
      (is (= 1 (db/create-event! t-conn (-> test-event-for-user
                                            (assoc :transaction-date (c/to-date (t/date-time 2017 03 24)))))))
      (let [recurring-expenses (db/get-recurring-expenses t-conn {:user-id user-id})]
        (is (not (empty? recurring-expenses)))
        (doseq [expense recurring-expenses]
          (is (= user-id (:user_id expense))))))))

(deftest test-recurring-events-with-same-transaction-id-are-all-added
  (jdbc/with-db-transaction [t-conn *db*]
    (jdbc/db-set-rollback-only! t-conn)
    (db/create-user!
     t-conn
     user)
    (let [user-id (:id (db/get-user-by-username t-conn {:username "test_person"}))]
      (is (= 1 (db/create-event! t-conn (assoc test-event :user-id user-id))))
      (is (= 1 (db/create-event! t-conn (-> test-event
                                            (assoc :user-id user-id)
                                            (assoc :transaction-date (c/to-date (t/date-time 2017 03 24)))))))
      (is (= 2 (count (db/get-events t-conn {:user-id user-id}))))
      (is (= 2 (count (db/get-recurring-expenses t-conn {:user-id user-id})))))))

(deftest test-saving-and-retrieving-savings-recipients
  (jdbc/with-db-transaction [t-conn *db*]
                            (jdbc/db-set-rollback-only! t-conn)
                            (db/create-user!
                              t-conn
                              user)
                            (let [user-id (:id (db/get-user-by-username t-conn {:username "test_person"}))]
                              (is (= 1 (db/save-savings! t-conn {:user-id user-id
                                                                 :recipients (to-db-array *db* ["A" "B"])})))
                              (is (= {:recipients ["A" "B"]}
                                     (db/get-savings t-conn {:user-id user-id})))
                              (is (= 1 (db/save-savings! t-conn {:user-id user-id
                                                                 :recipients (to-db-array *db* ["C" "D" "E"])})))
                              (is (= {:recipients ["C" "D" "E"]}
                                     (db/get-savings t-conn {:user-id user-id}))))))

