(ns my-money.services.op-csv
  (:require [my-money.csv :as csv]
            [my-money.services.bank-csv :as bank]))

(defmethod bank/read-bank-statement :op [file]
  (-> file
      (slurp :encoding "ISO-8859-1")
      csv/csv->clj))
