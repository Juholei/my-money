(ns my-money.services.spankki-csv
  (:require [clojure.string :as str]
            [my-money.csv :as csv]
            [my-money.services.bank-csv :as bank]))

(defmethod bank/read-bank-statement :spankki [file]
  (-> file
      slurp
      (str/replace "\uFEFF" "")
      csv/csv->clj))