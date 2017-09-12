(ns my-money.calculations)

(defn balance [events]
  (/ (reduce #(+ %1 (int (:amount %2))) 0 events) 100))

(defn expenses [events]
  (let [expense-events (filter #(< (:amount %) 0) events)]
    (balance expense-events)))

(defn income [events]
  (let [income-events (filter #(> (:amount %) 0) events)]
    (balance income-events)))

(defn sum-til-date
  "Calculates the sum of amounts in the events up to and including the given date"
  [date events]
  (let [events-before (filter #(<= (:transaction_date %) date) events)]
    (apply + (map :amount events-before))))
