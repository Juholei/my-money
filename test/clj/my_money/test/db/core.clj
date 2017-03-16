(ns my-money.test.db.core
  (:require [my-money.db.core :refer [*db*] :as db]
            [luminus-migrations.core :as migrations]
            [clojure.test :refer :all]
            [clojure.java.jdbc :as jdbc]
            [my-money.config :refer [env]]
            [mount.core :as mount]))

(use-fixtures
  :once
  (fn [f]
    (mount/start
     #'my-money.config/env
     #'my-money.db.core/*db*)
    (migrations/migrate ["migrate"] (select-keys env [:database-url]))
    (f)))

(def test-event {:id "2412231/876567/9412363"
                 :user-id ""
                 :transaction-date "24.01.2017"
                 :amount 500
                 :recipient ""
                 :type "123"})

(deftest test-users
  (jdbc/with-db-transaction [t-conn *db*]
    (jdbc/db-set-rollback-only! t-conn)
    (is (= 1 (db/create-user!
              t-conn
              {:username "test_person"})))
    (is (= "test_person" (:username (db/get-user-by-username t-conn {:username "test_person"}))))))

(deftest test-events
  (jdbc/with-db-transaction [t-conn *db*]
    (jdbc/db-set-rollback-only! t-conn)
    (db/create-user!
     t-conn
     {:username "test_person"})
    (let [user-id (:id (db/get-user-by-username t-conn {:username "test_person"}))]
      (is (= 1 (db/create-event! t-conn (assoc test-event :user-id user-id))))
      (is (= 1 (count (db/get-events t-conn {:user-id user-id})))))))


(deftest test-duplicate-events-are-not-added
  (jdbc/with-db-transaction [t-conn *db*]
    (jdbc/db-set-rollback-only! t-conn)
    (db/create-user!
     t-conn
     {:username "test_person"})
    (let [user-id (:id (db/get-user-by-username t-conn {:username "test_person"}))]
      (is (= 1 (db/create-event! t-conn (assoc test-event :user-id user-id))))
      (is (= 0 (db/create-event! t-conn (assoc test-event :user-id user-id)))))))
