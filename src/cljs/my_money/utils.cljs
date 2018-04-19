(ns my-money.utils
  (:require [clojure.string :as string]))

(defn amount->pretty-string [amount]
  (-> amount
      (/ 100)
      (.toFixed 2)
      (str "€")
      (string/replace-first "." ",")))

(defn date->pretty-string [date]
  (str (.getDate date)
       "."
       (inc (.getMonth date))
       "."
       (.getFullYear date)))

(defn events->recipient-types [events]
  (apply merge (set (map #(hash-map (:recipient %) (:type %)) (remove #(nil? (:type %)) events)))))
