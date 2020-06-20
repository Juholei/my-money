(ns my-money.routes.auth-test
  (:require [clojure.test :refer :all]
            [cheshire.core :refer [parse-string]]
            [buddy.hashers :as hashers]
            [ring.mock.request :refer :all]
            [my-money.handler :refer :all]))

(def events-request (request :get "/events"))

(def recurring-expenses-request (request :get "/events/recurring/expenses"))

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
       (is (= {:username "foo"} (parse-response body))))))

  (testing "Unsuccessful login"
    (with-redefs [my-money.db.core/get-user-by-username mock-get-user-by-username]
      (let [{:keys [body status]} ((app) (login-request "food" "bar"))]
        (is (= 401 status))
        (is (= {:result "unauthorized"
                :message "login failure"}
               (parse-response body)))))))

(deftest test-events-route
  (testing "Retrieving events is denied without login"
    (let [{:keys [body status]} ((app) events-request)]
      (is (= 401 status))
      (is (nil? body)))))

(deftest test-recurring-events-route
  (testing "Retrieving recurring events is denied without login"
    (let [{:keys [body status]} ((app) recurring-expenses-request)]
      (is (= 401 status))
      (is (nil? body)))))

(deftest test-upload-route
  (testing "Upload route access is denied without login"
    (let [{:keys [status body]} ((app) (request :post "/upload"))]
      (is (= 401 status))
      (is (nil? body)))))
