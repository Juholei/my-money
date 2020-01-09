(ns my-money.op-csv
  (:require [clojure.data.csv :as csv]
            [clojure.string :as str]
            [clj-time.format :as f]))

(defn read-csv [csv-string]
  (csv/read-csv csv-string :separator \;))

(defn remove-column
  "Removes item in given column number from each vector in the vector of vectors"
  [csv-vec column]
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

(defn- header->keyword
  "Removes whitespace from string originating from csv header and turns it into a keyword"
  [header]
  (-> header
      (str/replace " " "") ; There's some weird space in some column name
      (str/replace #"\s+" "")
      (keyword)))

(defn csv-vec->map [csv-vec]
  (mapv #(zipmap (mapv header->keyword (first csv-vec)) %1) (rest csv-vec)))

(defn date-string->date [string]
  (let [date-parser (partial f/parse (f/formatter "dd.MM.yyyy"))]
    (date-parser string)))
