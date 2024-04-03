(defproject otus-18 "0.1.0-SNAPSHOT"
  :description "FIXME: write description"
  :url "http://example.com/FIXME"

  :dependencies [[org.clojure/clojure "1.11.1"]
                 [org.clojure/core.async "1.6.673"]
                 ;; http & json
                 [clj-http "3.12.3"]
                 [clj-http-fake "1.0.4"]
                 [cheshire "5.11.0"]
                 ;; database
                 [com.h2database/h2 "2.2.224"]
                 [com.github.seancorfield/next.jdbc "1.3.909"]
                 [com.github.seancorfield/honeysql "2.5.1103"]
                 [dev.weavejester/ragtime "0.9.4"]
                 [com.zaxxer/HikariCP  "5.0.1"]
                 ]

  :main ^:skip-aot otus-18.homework.core
  :profiles {:uberjar {:aot :all
                       :jvm-opts ["-Dclojure.compiler.direct-linking=true"]}}
  )
