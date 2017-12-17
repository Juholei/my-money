(ns my-money.app.controller.navigation)

(defn set-in-progress [app in-progress?]
  (assoc app :in-progress in-progress?))
