(ns my-money.op-csv
  (:require [clojure.data.csv :as csv]))

(defn read-csv [csv-string]
  (csv/read-csv csv-string :separator \;))

(defn remove-column [csv-vec column]
  "Removes item in given column number from each vector in the vector of vectors"
  (let [reducer (fn [acc row]
                  (conj acc
                        (vec (concat (subvec row 0 column)
                                     (subvec row (inc column))))))]
    (reduce reducer [] csv-vec)))
