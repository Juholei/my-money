(ns my-money.csv-test
  (:require [clojure.test :refer [deftest is testing]]
            [my-money.csv :refer :all]))

(def test-csv "Kirjauspäivä;Arvopäivä;Määrä  EUROA;\"Laji\";Selitys;Saaja/Maksaja;Saajan tilinumero ja pankin BIC;Viite;Viesti;Arkistointitunnus;\n24.01.2017;24.01.2017;500,00;\"123\";TALLETUSAUTOM.;\"PERSON EXAMPLE\";;\"\";Käteistalletus automaatilla 12345 Setelit yht 500,00 EUR Setelit yht 1 kpl  ;2412231/876567/9412363")

(deftest test-csv-vec->map
  (testing "Converting Vector of vectors containing csv data to a vector of maps"
    (is (= [{:Kirjauspäivä "24.01.2017"
             :Arvopäivä "24.01.2017"
             :MääräEUROA "500,00"
             :Laji "123"
             :Selitys "TALLETUSAUTOM."
             :Saaja/Maksaja "PERSON EXAMPLE"
             :SaajantilinumerojapankinBIC ""
             :Viite ""
             :Viesti "Käteistalletus automaatilla 12345 Setelit yht 500,00 EUR Setelit yht 1 kpl  "
             :Arkistointitunnus "2412231/876567/9412363"}] (csv-vec->map (read-csv test-csv))))))
