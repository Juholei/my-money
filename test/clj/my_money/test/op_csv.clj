(ns my-money.test.op-csv
  (:require [clojure.test :refer :all]
            [my-money.op-csv :refer :all]))

(def test-csv "Kirjauspäivä;Arvopäivä;Määrä  EUROA;\"Laji\";Selitys;Saaja/Maksaja;Saajan tilinumero ja pankin BIC;Viite;Viesti;Arkistointitunnus;")

(deftest test-parsing-column-names-from-csv
  (testing "Column names"
    (is (= ["Kirjauspäivä" "Arvopäivä" "Määrä  EUROA" "Laji"
            "Selitys" "Saaja/Maksaja" "Saajan tilinumero ja pankin BIC"
            "Viite" "Viesti" "Arkistointitunnus" ""] (first (read-csv test-csv))))))
