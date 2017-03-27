(ns my-money.calculations)

(defn balance [events]
  (/ (reduce #(+ %1 (int (:amount %2))) 0 events) 100))

(defn expenses [events]
  (let [expense-events (filter #(< (:amount %) 0) events)]
    (balance expense-events)))

(defn income [events]
  (let [income-events (filter #(> (:amount %) 0) events)]
    (balance income-events)))
