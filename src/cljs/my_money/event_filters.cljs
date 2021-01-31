(ns my-money.event-filters)

(defn- event->month [event]
 (str (inc (.getMonth (:transaction_date event)))
      "."
      (.getFullYear (:transaction_date event))))

(defn months
  "Returns distincts month + year combinations from events, sorted descending"
  [events]
  (->> events
       (sort #(compare (.getTime (:transaction_date %2)) (.getTime (:transaction_date %1))))
       (map event->month)
       distinct))

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
