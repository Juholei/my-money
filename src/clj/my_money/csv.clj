(ns my-money.csv
  (:require [clojure.data.csv :as csv]
            [clojure.string :as str]))

(defn- header->keyword
  "Removes whitespace from string originating from csv header and turns it into a keyword"
  [header]
  (-> header
      (str/replace " " "") ; There's some weird space in some column name
      (str/replace #"\s+" "")
      (keyword)))

(defn read-csv [csv-string]
  (csv/read-csv csv-string :separator \;))

(defn csv-vec->map [csv-vec]
  (mapv #(zipmap (mapv header->keyword (first csv-vec)) %1) (rest csv-vec)))

(defn csv->clj [csv]
  (-> csv
      read-csv
      csv-vec->map))