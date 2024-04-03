(ns otus-18.homework.conf.migrations
  (:require [otus-18.homework.conf.config :refer [ctx]]
            [ragtime.jdbc :as jdbc]
            [ragtime.repl :as repl]))


(defn load-config []
  {:datastore  (jdbc/sql-database {:connection-uri (get-in @ctx [:db :url])})
   :migrations (jdbc/load-directory "./migrations")})
; почему-то (jdbc/load-load-resources "migrations") не работает

(defn migrate []
  (println "migrating ....")
  (repl/migrate (load-config))
  (println "migrated"))

(defn clear []
  (repl/rollback (load-config)))


(comment
  (migrate)
  )