(ns my-money.test.routes.config
  (:require [clojure.test :refer :all]
            [ring.mock.request :refer :all]
            [my-money.handler :refer :all]))

(def save-config-request (request :post "/save-config"))

(def get-config-request (request :get "/get-config"))

(deftest test-save-config-route
  (testing "Saving is not possible when not logged out"
    (let [{:keys [status body]} ((app) save-config-request)]
      (is (= 401 status))
      (is (nil? body)))))

(deftest test-get-config-route
  (testing "Getting config not possible when logged out"
    (let [{:keys [status body]} ((app) get-config-request)]
      (is (= 401 status))
      (is (nil? body)))))
