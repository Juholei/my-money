(ns my-money.components.button)

(def button-styles ["btn"
                    "btn-outline-info"
                    "ml-1"])

(defn button [props child]
  (let [props-with-default-styles (update props :class concat button-styles)]
    [:button props-with-default-styles child]))
