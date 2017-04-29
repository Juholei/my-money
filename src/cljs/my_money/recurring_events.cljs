(ns my-money.recurring-events)

(defn sort-recurring-events [events]
  (let [distinct-recipients (distinct (map :recipient events))]
    (vec (for [recipient distinct-recipients]
           (let [recipient-events (filter #(= recipient (:recipient %)) events)]
             {:recipient recipient
              :amount (:amount (first recipient-events))
              :events (vec recipient-events)})))))
