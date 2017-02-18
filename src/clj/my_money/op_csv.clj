(ns my-money.op-csv
  (:require [clojure.data.csv :as csv]))

(defn read-csv [csv-string]
  (csv/read-csv csv-string :separator \;))

(defn remove-column [csv-vec column]
  "Removes item in given column number from each vector in the vector of vectors"
  (let [remove-item (fn [acc row]
                  (if (>= (count row) (inc column))
                    (conj acc
                          (vec (concat (subvec row 0 column)
                                       (subvec row (inc column)))))
                    acc))]
    (reduce remove-item [] csv-vec)))

(defn remove-column-by-name [csv-vec column-name]
  (let [column-index (.indexOf (first csv-vec) column-name)]
    (if (= column-index -1)
      csv-vec
      (remove-column csv-vec column-index))))

(defn remove-columns-by-name [csv-vec column-names]
  (reduce remove-column-by-name csv-vec column-names))
