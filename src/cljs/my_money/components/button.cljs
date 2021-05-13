(ns my-money.components.button)

(def button-base-styles ["cursor-pointer"
                         "border-solid"
                         "border-2"
                         "ml-1"
                         "inline-block"
                         "py-1.5"
                         "px-3"
                         "rounded"
                         "hover:text-white"])

(def button-outline-styles ["bg-transparent"])

(def button-type-specific-styles {:outline button-outline-styles})

(defn button [{:keys [type] :as props} child]
  (let [button-styles (concat button-base-styles (get button-type-specific-styles type))
        props-with-default-styles (update props :class concat button-styles)]
    [:button props-with-default-styles child]))
