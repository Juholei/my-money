(ns my-money.utils-test
  (:require [cljs.test :refer-macros [is are deftest testing use-fixtures]]
            [my-money.utils :as utils]))

(deftest test-converting-cents-to-money-string
  (testing "Convert 10 to 0,10€"
    (is (= "0,10€"
           (utils/amount->pretty-string 10))))
  (testing "Convert 12345 to 123,45€"
    (is (= "123,45€"
           (utils/amount->pretty-string 12345))))
  (testing "Convert 2000 to 20,00€"
    (is (= "20,00€"
           (utils/amount->pretty-string 2000)))))
