(ns my-money.test.auth
  (:require [clojure.test :refer :all]
            [cheshire.core :refer [parse-string]]
            [buddy.hashers :as hashers]
            [ring.mock.request :refer :all]
            [my-money.handler :refer :all]))

(defn encode-auth [user pass]
  (->> (str user ":" pass)
       (.getBytes)
       (.encodeToString (java.util.Base64/getEncoder))
       (str "Basic ")))

(defn login-request [id pass]
  (-> (request :post "/login")
      (header "Authorization" (encode-auth id pass))))

(defn mock-get-user-by-username [{:keys [username]}]
  (if (= username "foo")
    {:username "foo"
     :password (hashers/encrypt "bar")}))

(defn parse-response [body]
  (-> body
      slurp
      (parse-string true)))

(deftest test-login-route
  (testing "Successful login"
    (with-redefs [my-money.db.core/get-user-by-username mock-get-user-by-username]
     (let [{:keys [body status] :as resp} ((app) (login-request "foo" "bar"))]
       (is (= 200 status))
       (is (= {:result "ok"} (parse-response body))))))

  (testing "Unsuccessful login"
    (with-redefs [my-money.db.core/get-user-by-username mock-get-user-by-username]
      (let [{:keys [body status]} ((app) (login-request "food" "bar"))]
        (is (= 401 status))
        (is (= {:result "unauthorized"
                :message "login failure"}
               (parse-response body)))))))
