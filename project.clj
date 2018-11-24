(defproject my-money "0.1.0-SNAPSHOT"

  :description "FIXME: write description"
  :url "http://example.com/FIXME"

  :dependencies [[bouncer "1.0.1"]
                 [buddy "2.0.0"]
                 [clj-time "0.15.1"]
                 [cljs-ajax "0.8.0"]
                 [cljsjs/react "15.6.1-2"]
                 [cljsjs/react-dom "15.6.1-2"]
                 [cljsjs/react-chartjs-2 "2.0.5-1" :exclusions [cljsjs/react]]
                 [compojure "1.6.1"]
                 [conman "0.8.3"]
                 [cprop "0.1.13"]
                 [funcool/bide "1.6.0"]
                 [luminus-immutant "0.2.4"]
                 [luminus-migrations "0.6.1"]
                 [luminus-nrepl "0.1.4"]
                 [markdown-clj "1.0.5"]
                 [metosin/ring-http-response "0.9.1"]
                 [metosin/muuntaja "0.6.1"]
                 [mount "0.1.14"]
                 [org.clojure/clojure "1.9.0"]
                 [org.clojure/clojurescript "1.10.439" :scope "provided"]
                 [org.clojure/data.csv "0.1.4"]
                 [org.clojure/tools.cli "0.4.1"]
                 [org.clojure/tools.logging "0.4.1"]
                 [org.postgresql/postgresql "42.2.5"]
                 [org.webjars.bower/tether "1.4.4"]
                 [org.webjars/bootstrap "4.1.3"]
                 [org.webjars/font-awesome "5.5.0"]
                 [org.webjars/webjars-locator-jboss-vfs "0.1.0"]
                 [reagent "0.7.0" :exclusions [cljsjs/react]]
                 [reagent-utils "0.2.1"]
                 [ring-middleware-format "0.7.2"]
                 [ring-webjars "0.2.0"]
                 [ring/ring-defaults "0.3.2"]
                 [selmer "1.12.5"]
                 [webjure/tuck "20180722"]]

  :min-lein-version "2.0.0"

  :jvm-opts ["-server" "-Dconf=.lein-env"]
  :source-paths ["src/clj" "src/cljc"]
  :resource-paths ["resources" "target/cljsbuild"]
  :target-path "target/%s/"
  :main my-money.core
  :migratus {:store :database :db ~(get (System/getenv) "DATABASE_URL")}

  :plugins [[lein-cprop "1.0.3"]
            [migratus-lein "0.6.8"]
            [lein-cljsbuild "1.1.7"]
            [lein-immutant "2.1.0"]]
  :clean-targets ^{:protect false}
  [:target-path [:cljsbuild :builds :app :compiler :output-dir] [:cljsbuild :builds :app :compiler :output-to]]
  :figwheel
  {:http-server-root "public"
   :nrepl-port 7002
   :css-dirs ["resources/public/css"]
   :nrepl-middleware [cemerick.piggieback/wrap-cljs-repl]}
  :repl-options {:port 7000}
  :profiles
  {:uberjar {:omit-source true
             :prep-tasks ["compile" ["cljsbuild" "once" "min"]]
             :cljsbuild
             {:builds
              {:min
               {:source-paths ["src/cljc" "src/cljs" "env/prod/cljs"]
                :compiler
                {:output-to "target/cljsbuild/public/js/app.js"
                 :optimizations :advanced
                 :pretty-print false
                 :closure-warnings
                 {:externs-validation :off :non-standard-jsdoc :off}
                 :externs ["react/externs/react.js"]}}}}


             :aot :all
             :uberjar-name "my-money.jar"
             :source-paths ["env/prod/clj"]
             :resource-paths ["env/prod/resources"]}

   :dev           [:project/dev :profiles/dev]
   :test          [:project/dev :project/test :profiles/test]

   :project/dev  {:dependencies [[prone "1.6.1"]
                                 [ring/ring-mock "0.3.2"]
                                 [ring/ring-devel "1.7.1"]
                                 [pjstadig/humane-test-output "0.9.0"]
                                 [binaryage/devtools "0.9.10"]
                                 [com.cemerick/piggieback "0.2.2"]
                                 [doo "0.1.11"]
                                 [figwheel-sidecar "0.5.17"]]
                  :plugins      [[com.jakemccrary/lein-test-refresh "0.23.0"]
                                 [lein-doo "0.1.11"]
                                 [lein-figwheel "0.5.17"]
                                 [org.clojure/clojurescript "1.10.439"]]
                  :cljsbuild
                  {:builds
                   {:app
                    {:source-paths ["src/cljs" "src/cljc" "env/dev/cljs"]
                     :compiler
                     {:main "my-money.app"
                      :asset-path "/js/out"
                      :output-to "target/cljsbuild/public/js/app.js"
                      :output-dir "target/cljsbuild/public/js/out"
                      :source-map true
                      :optimizations :none
                      :pretty-print true}}}}



                  :doo {:build "test"}
                  :source-paths ["env/dev/clj" "test/clj"]
                  :resource-paths ["env/dev/resources"]
                  :repl-options {:init-ns user}
                  :injections [(require 'pjstadig.humane-test-output)
                               (pjstadig.humane-test-output/activate!)]}
   :project/test {:resource-paths ["env/test/resources"]
                  :cljsbuild
                  {:builds
                   {:test
                    {:source-paths ["src/cljc" "src/cljs" "test/cljs"]
                     :compiler
                     {:output-to "target/test.js"
                      :main "my-money.doo-runner"
                      :optimizations :whitespace
                      :pretty-print true
                      :process-shim false}}}}}


   :profiles/dev {}
   :profiles/test {}})
