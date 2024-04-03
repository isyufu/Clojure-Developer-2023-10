(ns otus-18.homework.db
  (:require [honey.sql :as sql]
            [honey.sql.helpers :refer [from insert-into values select values where]]
            [next.jdbc :as jdbc]
            [next.jdbc.connection :as connection]
            [next.jdbc.sql :as jdbc.sql]
            [next.jdbc.result-set :as rs]
            [otus-18.homework.conf.config :refer [ctx]]
            )
  )

(def ds (connection/->pool com.zaxxer.hikari.HikariDataSource
                           {:jdbcUrl (get-in @ctx [:db :url])
                            }))

(defn save-type [map]
  (let [
        types-maps (select-keys map [:id :name])
        transl-types-maps (select-keys map [:id :lang :translate])
        ]
    (jdbc.sql/insert! ds :types types-maps)
    (jdbc.sql/insert! ds :translate_types transl-types-maps)
    )
  ;; todo ignore on conflict
  )

(defn select-type-id-by-name [n]
  (-> (select :id)
      (from :types)
      (where [:= :name n])))

(defn save-pokemon [poke]
  ;; todo ignore on conflict
  (let [
        pokemon (select-keys poke [:id :name])
        id (:id poke)
        poke-sql (-> (insert-into :pokemons)                ;sql для вставки покемона
                     (values [pokemon]))
        pt-values (mapv (fn [n] {:id_poke id :id_type (select-type-id-by-name n)}) (:types poke))
        pt-sql (-> (insert-into :poke-types)
                   (values pt-values))                      ; sql для вставки в poke-types
        ]
    (jdbc/execute! ds (sql/format poke-sql))
    (jdbc/execute! ds (sql/format pt-sql))
    )
  )

(defn execute [sql]
  (jdbc/execute! ds sql {:builder-fn rs/as-unqualified-lower-maps}))