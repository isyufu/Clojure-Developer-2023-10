(defproject game-2048 "0.1.0-SNAPSHOT"
  :description "2048"

  :min-lein-version "2.9.1"

  :dependencies [[org.clojure/clojure "1.11.0"]
                 [org.clojure/clojurescript "1.10.773"]
                 [org.clojure/core.async  "0.4.500"]
                 [reagent "1.0.0"]
                 [reagent-utils "0.3.8"]
                 ]

  :plugins [[lein-figwheel "0.5.20"]
            [lein-cljsbuild "1.1.7" :exclusions [[org.clojure/clojure]]]]

  :source-paths ["src"]

  :cljsbuild {:builds
              [{:id "dev"
                :source-paths ["src"]
                :figwheel {:on-jsload "game-2048.core/on-js-reload"
                           ;:open-urls ["http://localhost:3449/index.html"]
                           }
                :compiler {:main game-2048.core
                           :asset-path "js/compiled/out"
                           :output-to "resources/public/js/compiled/game_2048.js"
                           :output-dir "resources/public/js/compiled/out"
                           :source-map-timestamp true
                           :preloads [devtools.preload]}}
               {:id "min"
                :source-paths ["src"]
                :compiler {:output-to "resources/public/js/compiled/game_2048.js"
                           :main game-2048.core
                           :optimizations :advanced
                           :pretty-print false}}]}

  :figwheel {             :css-dirs ["resources/public/css"]             }

  :profiles {:dev {:dependencies [[binaryage/devtools "1.0.0"]
                                  [figwheel-sidecar "0.5.20"]]
                   ;; need to add dev source path here to get user.clj loaded
                   :source-paths ["src"]
                   ;; need to add the compiled assets to the :clean-targets
                   :clean-targets ^{:protect false} ["resources/public/js/compiled"
                                                     :target-path]}})
