(ns my-money.calculations)

(defn balance
  "Calculates the sum of all amounts in the events.
   Returns the sum in euros, while input is in cents"
  [events]
  (/ (reduce #(+ %1 (int (:amount %2))) 0 events) 100))

(defn savings
  "Calculates the sum of events which have their recipient
   in savings-recipients-set"
  [events savings-recipients-set]
  (let [savings-events (filter #(contains? savings-recipients-set (:recipient %)) events)]
    (Math/abs (balance savings-events))))

(defn expenses
  "Calculates the sum of expenses that have a recipient
   which is not part of the savings-recipients-set"
  [events savings-recipients-set]
  (let [expense-events (filter #(< (:amount %) 0) events)
        all-expenses-sum (balance expense-events)
        savings-sum (savings events savings-recipients-set)]
    (+ all-expenses-sum savings-sum)))

(defn income
  "Calculates the sum of events which have a positive amount"
  [events]
  (let [income-events (filter #(> (:amount %) 0) events)]
    (balance income-events)))

(defn sum-til-date
  "Calculates the sum of amounts in the events up to and including the given date"
  [date events]
  (let [events-before (filter #(<= (:transaction_date %) date) events)]
    (apply + (map :amount events-before))))
