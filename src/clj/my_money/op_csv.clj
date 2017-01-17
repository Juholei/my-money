(ns my-money.op-csv
  (:require [clojure.data.csv :as csv]))

(defn read-csv [csv-string]
    (csv/read-csv csv-string :separator \;))
