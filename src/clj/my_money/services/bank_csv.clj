(ns my-money.services.bank-csv
  (:require [clojure.java.io :as io]
            [clojure.string :as str]
            [clj-time.format :as tf]))

(def op-first-line-hash 96410471)

(def s-pankki-first-line-hash -1695375124)

(defn date-string->date [string]
  (let [date-parser (partial tf/parse (tf/formatter "dd.MM.yyyy"))]
    (date-parser string)))

(defn ->event [user-id raw-data]
  {:transaction-id (:Arkistointitunnus raw-data)
   :user-id user-id
   :transaction-date (date-string->date (:Kirjauspäivä raw-data))
   :amount (Integer/parseInt (str/replace (:MääräEUROA raw-data) "," ""))
   :recipient (:Saaja/Maksaja raw-data)
   :type (:Laji raw-data)})

(defn ->events [x]
  (map (partial ->event 1) x))

(defn detect-bank
  "Takes column line as a string from a csv file and checks if it matches some known bank file"
  [column-line]
  (cond
    (= s-pankki-first-line-hash (hash column-line)) :spankki
    (= op-first-line-hash (hash column-line)) :op
    :else :not-found))

(defn detect-bank-from-file
  "Reads the first line of the file in the given path
  and based on it tries to detech what bank the file is from."
  [filepath]
  (with-open [reader (io/reader filepath)]
    (-> reader
        line-seq
        first
        detect-bank)))

(defmulti read-bank-statement detect-bank-from-file)
