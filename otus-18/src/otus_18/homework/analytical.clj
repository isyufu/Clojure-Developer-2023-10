(ns otus-18.homework.analytical
  (:require
    [otus-18.homework.db :refer [execute]]
    [otus-18.homework.sql-builder :refer [columns-fn from-fn fun-fn
                                          group-by-fn left-join-fn order-by-fn
                                          to-sql
                                          ]]
    ))

(defn total-pokemons []
  (-> {}
      (from-fn :pokemons)
      (columns-fn [[(fun-fn :count 1) :cnt]])
      to-sql
      execute
      (get-in [0 :cnt])))

(defn total-types []
  (-> {}
      (from-fn :types)
      (columns-fn [[(fun-fn :count 1) :cnt]])
      to-sql
      execute
      (get-in [0 :cnt])))


"
select count(1) as cnt, t.NAME
from POKE_TYPES pt
left join types t on pt.ID_TYPE = t.id
group by ID_TYPE ORDER BY cnt desc
"
(defn popular-types []
  (-> {}
      (columns-fn [[(fun-fn :count 1) :cnt] [:t.name]])
      (from-fn [[:poke_types :pt]])
      (left-join-fn [[:types :t] [:= :pt.id_type :t.id]])
      (group-by-fn [:id_type])
      (order-by-fn [[:cnt :desc]])
      to-sql
      execute)
  )

(defn support-languages []
  (let [result (-> {}
                   (columns-fn [(fun-fn :distinct :lang)])
                   (from-fn [:translate_types])
                   to-sql
                   execute
                   )
        ]
    (mapv #(:lang %) result)))

(defn analytics-print []
  (println "Всего покемонов загружено" (total-pokemons))
  (println "Всего типов покемонов" (total-types))
  (println "Поддерживаемые языки кроме английского" (support-languages))
  (println "Наиболее часто встречаемые типы покемонов" (popular-types))
  )
(comment
  (total-pokemons)
  (total-types)
  (popular-types)
  (support-languages)
  (analytics-print)
  )