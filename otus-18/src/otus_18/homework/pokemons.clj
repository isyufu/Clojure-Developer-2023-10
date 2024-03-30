(ns otus-18.homework.pokemons
  (:require [cheshire.core :as cheshire]
            [clj-http.client :as client]
            [clojure.core.async :as a :refer [<! <!! >!
                                              chan close!
                                              go onto-chan!
                                              thread]]))

(def base-url "https://pokeapi.co/api/v2")
(def pokemons-url (str base-url "/pokemon"))
(def type-path (str base-url "/type"))

(def n-concurency 5)
(defn extract-pokemon-name [pokemon]
  (:name pokemon))

(defn extract-type-name [pokemon-type lang]
  (->> (:names pokemon-type)
       (filter (fn [type-name] (= lang (-> type-name :language :name))))
       (first)
       :name))


(defn async-get [url]
  (thread (client/get url)))

(defn parse-str [s]
  (cheshire/parse-string s true))

(defn get-parse-xform [url xform]
  (->> (client/get url)
       :body
       parse-str
       xform))

(defn get-and-parse
  "xform may be nil"
  [url & xform]
  (go (as-> (<! (async-get url)) m
            (:body m)
            (cheshire/parse-string m true)
            (if (some? xform) ((first xform) m) m))))

; генерируем урлы получения списка покемонов, например по 20 штук
(defn generate-pokemon-urls
  ([total] (generate-pokemon-urls total 20))
  ([total batch-size]
   (map (fn [offset]
          (str pokemons-url "?offset=" offset "&limit=" (min batch-size (- total offset))))
        (range 0 total batch-size))
   ))

(defn translated-types
  "Получение переведенных типов"
  [lang]
  (let [in> (chan)
        out> (chan)
        blocking-get-type-name (fn [u] (get-parse-xform u (fn [r] [(:name r) (extract-type-name r lang)])))]

    (a/pipeline-blocking n-concurency out> (map blocking-get-type-name) in>)

    (go (->> (<! (get-and-parse type-path))
             :results
             (map :url)
             (onto-chan! in>)))

    (let [result (<!! (a/into {} out>))]
      result)))

(defn pokemons-types [count lang]
  (let [type-names-lang  (translated-types lang)      ; синхронное получение типов 2,5 сек
        batch-size 20
        in> (chan)
        mdl> (chan)
        mdl-2> (chan)
        out> (chan)

        ; получаем списки url покемонов (по batch-size)
        bl-get-pokes (fn [url] (get-parse-xform url :results))

        ; можно сказать flatten, pipe массивов в пайп элементов из массивов
        async-fn (fn [arr out*]
                   (go (doseq [x arr]
                         (>! out* x))
                       (close! out*)))

        ; получаем покемона, и ищем его тип в type-names-lang
        bl-parse-poke (fn [{name :name url :url}]
                        (let [
                              body-types (get-parse-xform url :types)
                              type-names (mapv (fn [t] (type-names-lang (get-in t [:type :name]))) body-types)]
                          {:name name :types type-names}))]
    ; пайплайны
    (a/pipeline-blocking n-concurency mdl> (map bl-get-pokes) in>)
    (a/pipeline-async n-concurency mdl-2> async-fn mdl>)
    (a/pipeline-blocking n-concurency out> (map bl-parse-poke) mdl-2>)

    ; начало обработки
    (go (a/onto-chan! in> (generate-pokemon-urls count batch-size))) ; генерируем урлы

    (let [result (<!! (a/into [] out>))]
      result)))

(defn get-pokemons
  "Асинхронно запрашивает список покемонов и название типов в заданном языке. Возвращает map, где ключами являются
  имена покемонов (на английском английский), а значения - коллекция названий типов на заданном языке."
  [& {:keys [limit lang] :or {limit 50 lang "ja"}}]
  (pokemons-types limit lang))

(comment
  (generate-pokemon-urls 55)
  (time (pokemons-types 55 "ja"))
  )
