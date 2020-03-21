(defproject my-money "0.1.0-SNAPSHOT"

  :description "FIXME: write description"
  :url "http://example.com/FIXME"

  :dependencies [[bouncer "1.0.1"]
                 [buddy "2.0.0"]
                 [clj-time "0.15.2"]
                 [cljs-ajax "0.8.0"]
                 [cljsjs/react-chartjs-2 "2.7.4-0" :exclusions [cljsjs/react]]
                 [compojure "1.6.1"]
                 [conman "0.8.3"]
                 [cprop "0.1.14"]
                 [funcool/bide "1.6.0"]
                 [luminus-immutant "0.2.5"]
                 [luminus-migrations "0.6.1"]
                 [luminus-nrepl "0.1.6"]
                 [metosin/ring-http-response "0.9.1"]
                 [metosin/muuntaja "0.6.4"]
                 [mount "0.1.16"]
                 [org.clojure/clojure "1.10.1"]
                 [org.clojure/clojurescript "1.10.520" :scope "provided"]
                 [org.clojure/data.csv "0.1.4"]
                 [org.clojure/tools.cli "0.4.2"]
                 [org.clojure/tools.logging "0.5.0"]
                 [org.postgresql/postgresql "42.2.6"]
                 [org.webjars/bootstrap "4.2.1"]
                 [org.webjars/font-awesome "5.6.3"]
                 [org.webjars/webjars-locator-jboss-vfs "0.1.0"]
                 [reagent "0.10.0" :exclusions [cljsjs/react cljsjs/react-dom]]
                 [cljsjs/react "16.13.0-0"]
                 [cljsjs/react-dom "16.13.0-0"]
                 [reagent-utils "0.3.3"]
                 [ring-middleware-format "0.7.4"]
                 [ring-webjars "0.2.0"]
                 [ring/ring-defaults "0.3.2"]
                 [selmer "1.12.13"]
                 [webjure/tuck "20181204"]]

  :min-lein-version "2.0.0"

  :jvm-opts ["-server" "-Dconf=.lein-env"]
  :source-paths ["src/clj" "src/cljc"]
  :resource-paths ["resources" "target/cljsbuild"]
  :target-path "target/%s/"
  :main my-money.core
  :migratus {:store :database :db ~(get (System/getenv) "DATABASE_URL")}

  :plugins [[migratus-lein "0.6.8"]
            [lein-cljsbuild "1.1.7"]
            [lein-immutant "2.1.0"]]
  :clean-targets ^{:protect false}
  [:target-path [:cljsbuild :builds :app :compiler :output-dir] [:cljsbuild :builds :app :compiler :output-to]]
  :figwheel
  {:http-server-root "public"
   :nrepl-port 7002
   :css-dirs ["resources/public/css"]
   :nrepl-middleware [cider.piggieback/wrap-cljs-repl]}
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

   :project/dev  {:dependencies [[prone "2019-07-08"]
                                 [ring/ring-mock "0.4.0"]
                                 [ring/ring-devel "1.7.1"]
                                 [pjstadig/humane-test-output "0.9.0"]
                                 [binaryage/devtools "0.9.10"]
                                 [cider/piggieback "0.4.1"]
                                 [doo "0.1.11"]
                                 [figwheel-sidecar "0.5.19"]]
                  :plugins      [[com.jakemccrary/lein-test-refresh "0.24.1"]
                                 [lein-doo "0.1.11"]
                                 [lein-figwheel "0.5.19"]
                                 [lein-nvd "1.2.0"]]
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
                      :pretty-print true
                      :process-shim true}}}}
                  :doo {:build "test"
                        :paths {:karma "./node_modules/karma/bin/karma"}}
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
                      :optimizations :none
                      :pretty-print true
                      :process-shim true
                      :npm-deps {:karma "3.1.1"
                                 :karma-cljs-test "0.1.0"
                                 :karma-chrome-launcher "2.2.0"}
                      :install-deps true}}}}}


   :profiles/dev {}
   :profiles/test {}}
  :aliases {"fronttests-once" ["with-profile" "test" "doo" "chrome-headless" "once"]
            "fronttests" ["with-profile" "test" "doo" "chrome-headless"]})
