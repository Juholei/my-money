(ns my-money.event-filters)

(defn- event->month [event]
  (subs (:transaction_date event) 3))

(defn months [events]
  (set (map event->month events)))

(defn- event-type->filter [event-type]
  (condp = event-type
    "all" #(not= % 0)
    "expenses" neg?
    "incomes" pos?))

(defn- month->filter [month-to-filter]
  (fn [month]
    (cond
      (= month month-to-filter) true
      (= "All-time" month-to-filter) true
      :else false)))

(defn event-type-filter [event-type]
  (let [event-type-filter (event-type->filter event-type)]
    (fn [event]
      (event-type-filter (:amount event)))))

(defn month-filter [month]
  (let [wanted-month-filter (month->filter month)]
    (fn [event]
      (wanted-month-filter (event->month event)))))

(defn combined-filter [{:keys [month type]}]
  (let [p1 (month-filter month)
        p2 (event-type-filter type)]
    (every-pred p1 p2)))
