(ns otus-18.homework.core
  (:require
    [otus-18.homework.analytical :refer [analytics-print]]
    [otus-18.homework.conf.migrations :refer [migrate]]
    [otus-18.homework.db :refer [save-pokemon save-type]]
    [otus-18.homework.pokemons :refer [pokemons translated-types]]
    )
  (:gen-class))
(defn load-types []
  (println "loading types...")
  (translated-types "ja" save-type)                         ; can use println
  (println "saved")
  )
(defn load-pokemons []
  (println "loading pokemons...")
  (pokemons 55 save-pokemon)                                ; can use println
  (println "saved")
  )

(defn init []
  (do
    (migrate)
    (load-types)
    (load-pokemons)
    ))



(defn -main
  [& args]
  (do
    (init)
    (println "Аналитика")
    (analytics-print)
    (println "Нужно удалить файл ~/pokemons.h2.mv.db")
    )
  )
