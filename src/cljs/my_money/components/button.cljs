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

(def button-primary-styles ["text-white"
                            "bg-button-primary"
                            "border-transparent"
                            "hover:bg-button-primary-hover"
                            "active:bg-button-primary-hover"])

(def button-danger-styles ["text-white"
                           "bg-button-danger"
                           "border-button-danger"
                           "hover:bg-button-danger-hover"
                           "hover:border-button-danger-secondary"])

(def button-type-specific-styles {:primary button-primary-styles
                                  :outline button-outline-styles
                                  :danger button-danger-styles})

(defn button [{:keys [type] :as props} child]
  (let [button-styles (concat button-base-styles (get button-type-specific-styles type))
        props-with-default-styles (-> props
                                      (update :class concat button-styles)
                                      (dissoc :type))]
    [:button props-with-default-styles child]))
