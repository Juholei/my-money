(ns my-money.services.op-csv
  (:require [clojure.string :as str]
            [my-money.csv :as csv]
            [my-money.services.bank-csv :as bank]))

(defn ->event [raw-data]
  {:transaction-id (:Arkistointitunnus raw-data)
   :transaction-date (bank/date-string->date (:Kirjauspäivä raw-data))
   :amount (Integer/parseInt (str/replace (:MääräEUROA raw-data) "," ""))
   :recipient (:Saaja/Maksaja raw-data)
   :type (:Laji raw-data)})

(defn ->events [data-from-csv]
  (map ->event data-from-csv))

(defmethod bank/read-bank-statement :op [file]
  (-> file
      (slurp :encoding "ISO-8859-1")
      csv/csv->clj
      ->events))
