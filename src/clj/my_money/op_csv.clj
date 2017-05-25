(ns my-money.op-csv
  (:require [clojure.data.csv :as csv]
            [clj-time.format :as f]))

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

(defn- header->keyword [header]
  "Removes whitespace from string originating from csv header and turns it into a keyword"
  (-> header
      (clojure.string/replace " " "") ; There's some weird space in some column name
      (clojure.string/replace #"\s+" "")
      (keyword)))

(defn csv-vec->map [csv-vec]
  (mapv #(zipmap (mapv header->keyword (first csv-vec)) %1) (rest csv-vec)))

(defn date-string->date [string]
  (let [date-parser (partial f/parse (f/formatter "dd.MM.yyyy"))]
    (date-parser string)))
