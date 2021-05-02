(ns my-money.components.button)

(def button-styles ["cursor-pointer"
                    "border-solid"
                    "border-2"
                    "ml-1"
                    "inline-block"
                    "bg-transparent"
                    "py-1.5"
                    "px-3"
                    "rounded"
                    "hover:text-white"])

(defn button [props child]
  (let [props-with-default-styles (update props :class concat button-styles)]
    [:button props-with-default-styles child]))
