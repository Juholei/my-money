(ns my-money.test.op-csv
  (:require [clojure.test :refer :all]
            [my-money.op-csv :refer :all]))

(def test-csv "Kirjauspäivä;Arvopäivä;Määrä  EUROA;\"Laji\";Selitys;Saaja/Maksaja;Saajan tilinumero ja pankin BIC;Viite;Viesti;Arkistointitunnus;\n24.01.2017;24.01.2017;500,00;\"123\";TALLETUSAUTOM.;\"PERSON EXAMPLE\";;\"\";Käteistalletus automaatilla 12345 Setelit yht 500,00 EUR Setelit yht 1 kpl  ;2412231/876567/9412363")

(deftest test-parsing-column-names-from-csv
  (testing "Column names"
    (is (= ["Kirjauspäivä" "Arvopäivä" "Määrä  EUROA" "Laji"
            "Selitys" "Saaja/Maksaja" "Saajan tilinumero ja pankin BIC"
            "Viite" "Viesti" "Arkistointitunnus" ""] (first (read-csv test-csv))))))

(deftest test-removing-columns-from-parsed-csv
  (testing "Removing a column"
    (is (= [["Kirjauspäivä" "Määrä  EUROA" "Laji"
             "Selitys" "Saaja/Maksaja" "Saajan tilinumero ja pankin BIC"
             "Viite" "Viesti" "Arkistointitunnus" ""]
            ["24.01.2017" "500,00" "123" "TALLETUSAUTOM." "PERSON EXAMPLE" "" "" "Käteistalletus automaatilla 12345 Setelit yht 500,00 EUR Setelit yht 1 kpl  " "2412231/876567/9412363"]]
           (remove-column (read-csv test-csv) 1)))))

(deftest test-removing-column-by-name
  (testing "Removing column with given name"
    (is (= [["Kirjauspäivä" "Määrä  EUROA" "Laji"
             "Selitys" "Saaja/Maksaja" "Saajan tilinumero ja pankin BIC"
             "Viite" "Viesti" "Arkistointitunnus" ""]
            ["24.01.2017" "500,00" "123" "TALLETUSAUTOM." "PERSON EXAMPLE" "" "" "Käteistalletus automaatilla 12345 Setelit yht 500,00 EUR Setelit yht 1 kpl  " "2412231/876567/9412363"]]
           (remove-column-by-name (read-csv test-csv) "Arvopäivä")))))

(deftest test-giving-incorrect-column-name-removes-nothing
  (testing "Giving incorrect column name removes nothing"
    (is (= [["Kirjauspäivä" "Arvopäivä" "Määrä  EUROA" "Laji"
             "Selitys" "Saaja/Maksaja" "Saajan tilinumero ja pankin BIC"
             "Viite" "Viesti" "Arkistointitunnus" ""]
            ["24.01.2017" "24.01.2017" "500,00" "123" "TALLETUSAUTOM." "PERSON EXAMPLE" "" "" "Käteistalletus automaatilla 12345 Setelit yht 500,00 EUR Setelit yht 1 kpl  " "2412231/876567/9412363"]]
           (remove-column-by-name (read-csv test-csv) "VERY IMPORTANT COLUMN")))))

(deftest test-removing-last-column-from-parsed-csv
  (testing "Removing last column by name"
    (is (= [["Kirjauspäivä" "Arvopäivä" "Määrä  EUROA" "Laji"
             "Selitys" "Saaja/Maksaja" "Saajan tilinumero ja pankin BIC"
             "Viite" "Viesti" ""]
            ["24.01.2017" "24.01.2017" "500,00" "123" "TALLETUSAUTOM." "PERSON EXAMPLE" "" "" "Käteistalletus automaatilla 12345 Setelit yht 500,00 EUR Setelit yht 1 kpl  "]]
           (remove-column-by-name (read-csv test-csv) "Arkistointitunnus")))))

(deftest test-removing-last-column-from-parsed-csv-when-it-has-empty-lines
  (testing "Removing last column by name when there are empty lines present"
    (is (= [["Kirjauspäivä" "Arvopäivä" "Määrä  EUROA" "Laji"
             "Selitys" "Saaja/Maksaja" "Saajan tilinumero ja pankin BIC"
             "Viite" "Viesti" ""]
            ["24.01.2017" "24.01.2017" "500,00" "123" "TALLETUSAUTOM." "PERSON EXAMPLE" "" "" "Käteistalletus automaatilla 12345 Setelit yht 500,00 EUR Setelit yht 1 kpl  "]]
           (remove-column-by-name (read-csv (str test-csv "\n\n\n")) "Arkistointitunnus")))))

(deftest test-removing-multiple-columns-from-parsed-csv
  (testing "Revoming multiple columns"
    (is (= [["Määrä  EUROA" "Laji"
             "Selitys" "Saaja/Maksaja" "Saajan tilinumero ja pankin BIC"
             "Viite" "Viesti" "Arkistointitunnus" ""]
            ["500,00" "123" "TALLETUSAUTOM." "PERSON EXAMPLE" "" "" "Käteistalletus automaatilla 12345 Setelit yht 500,00 EUR Setelit yht 1 kpl  " "2412231/876567/9412363"]]
            (remove-columns-by-name (read-csv test-csv) ["Kirjauspäivä" "Arvopäivä"])))))
