(ns otus-18.homework.sql-builder
  (:require [honey.sql :as sql]))

"
Clojure не java, как упростить даже не представляю.
Все равно ерунда выходит.
"

"
Такое использование больше похоже на паттерн builder
Но оно ни чем не отличается от самого HoneySql
( -> {}
 (from-fn :table-name)
 (columns-fn [:name :id])
 (to-sql)
)
"
(defn from-fn [m v] (if (some? v) (assoc m :from v) m))
(defn columns-fn [m v] (if (some? v) (assoc m :select v) m))
(defn where-fn [m v] (if (some? v) (assoc m :where v) m))
(defn left-join-fn [m v] (if (some? v) (assoc m :left-join v) m))
(defn group-by-fn [m v] (if (some? v) (assoc m :group-by v) m))
(defn having-fn [m v] (if (some? v) (assoc m :having v) m))
(defn order-by-fn [m v] (if (some? v) (assoc m :order-by v) m))
(defn to-sql [m] (sql/format m))

(defn fun-fn
  "( -> {}
       (from-fn :table-name)
       (columns-fn [(fun-fn :min :price)])
       (to-sql)
       )
  "
  [func column]
  [[func column]]
  )


(defn builder-sql-select
  "Еще вариант, но тоже как мне кажется не удобный.
  ненужные параметры нужно заполнять nil.
  а если принимать мапу с ключами, то это тот же HoneySql

  table и columns обязательны"
  [table, columns, where, left-join, group-by, having]
  (let []
    (-> {}
        (from-fn table)
        (columns-fn columns)
        (where-fn where)
        (left-join-fn left-join)
        (group-by-fn group-by)
        (having-fn having)
        (to-sql)
        )
    ))

(comment
  (builder-sql-select :pets [:id :name] [:= :type :dog] nil nil nil)

  (-> {}
      (from-fn :table-name)
      (columns-fn [(fun-fn :min :price)])
      (to-sql)
      )

  )