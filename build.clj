(ns build
  (:require [clojure.tools.build.api :as b]))

(def lib 'my-money)
(def class-dir "target/classes")
(def uber-file "my-money.jar")
(def basis (delay (b/create-basis {:aliases [:prod]})))
(def src-dirs ["src/clj" "src/cljc" "env/prod/clj"])
(def resource-dirs ["resources" "env/prod/resources" "target/cljsbuild"])

(defn clean [_]
  (b/delete {:path "target/classes"})
  (b/delete {:path uber-file}))

(defn uber [_]
  (clean nil)
  (b/copy-dir {:src-dirs   resource-dirs
               :target-dir class-dir})
  (b/compile-clj {:basis     @basis
                  :src-dirs  src-dirs
                  :class-dir class-dir})
  (b/uber {:class-dir class-dir
           :uber-file uber-file
           :basis     @basis
           :main      'my-money.core}))
