(ns my-money.event-filters)

(defn months [events]
  (let [month-from-event (fn [event]
                          (subs (:transaction_date event) 3))]
    (set (map month-from-event events))))

(defn event-type->filter [event-type]
  (condp = event-type
    "all" #(not= % 0)
    "expenses" neg?
    "incomes" pos?))

(defn month->filter [month-to-filter]
  (fn [month]
    (cond
      (= month month-to-filter) true
      (= "All-time" month-to-filter) true
      :else false)))