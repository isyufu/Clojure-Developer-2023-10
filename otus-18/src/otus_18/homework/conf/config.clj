(ns otus-18.homework.conf.config)

(def ctx (atom {
                :db {:url "jdbc:h2:file:~/pokemons.h2"}
                }))