(ns my-money.services.spankki-csv
  (:require [clojure.string :as str]
            [my-money.csv :as csv]
            [my-money.services.bank-csv :as bank]))

(defn ->event [raw-data]
  (let [amount (Integer/parseInt (str/replace (:Summa raw-data) "," ""))]
    {:transaction-id (:Arkistointitunnus raw-data)
     :transaction-date (bank/date-string->date (:Kirjauspäivä raw-data))
     :amount amount
     :recipient (if (and (pos? amount) (not= "-" (:Maksaja raw-data)))
                  (:Maksaja raw-data)
                  (:Saajannimi raw-data))
     :type (:Tapahtumalaji raw-data)}))

(defn ->events [data-from-csv]
  (map ->event data-from-csv))

(defmethod bank/read-bank-statement :spankki [file]
  (-> file
      slurp
      (str/replace "\uFEFF" "")
      csv/csv->clj
      ->events))