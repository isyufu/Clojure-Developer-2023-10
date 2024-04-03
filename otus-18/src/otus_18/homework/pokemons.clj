(ns otus-18.homework.pokemons
  (:require [cheshire.core :as cheshire]
            [clj-http.client :as client]
            [clojure.core.async :as a :refer [<! <!! >!
                                              chan close!
                                              go go-loop onto-chan!
                                              thread]]))

(def base-url "https://pokeapi.co/api/v2")
(def pokemons-url (str base-url "/pokemon"))
(def type-path (str base-url "/type"))

(def n-concurency 5)

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
  "Получение переведенных типов.
  {:id :name :lang :translate} для save-xform"
  [lang save-xform]
  (let [in> (chan)
        out> (chan)
        blocking-get-type-name (fn [u] (get-parse-xform u (fn [r] {:name (:name r) :lang lang :id (:id r) :translate (extract-type-name r lang)})))]

    (a/pipeline-blocking n-concurency out> (map blocking-get-type-name) in>)

    (go (->> (<! (get-and-parse type-path))
             :results
             (map :url)
             (onto-chan! in>)))

    (<!! (go-loop []
           (when-let [x (<! out>)]
             (save-xform x)
             (recur))
           )))
  )

(defn pokemons [total save-xform]
  "Получение покемонов.
   {:id :name :types[:name]} для save-xform"
  (let [
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
        ; получаем покемона,
        bl-parse-poke (fn [{name :name url :url}]
                        (let [
                              body (get-parse-xform url (fn [m] (select-keys m [:types :id])))
                              id (:id body)
                              type-names (mapv (fn [t] (get-in t [:type :name])) (:types body))

                              ]
                          {:id id :name name :types type-names}
                          ))
        ]

    ; пайплайны
    (a/pipeline-blocking n-concurency mdl> (map bl-get-pokes) in>)
    (a/pipeline-async n-concurency mdl-2> async-fn mdl>)
    (a/pipeline-blocking n-concurency out> (map bl-parse-poke) mdl-2>)

    ; начало обработки
    (go (a/onto-chan! in> (generate-pokemon-urls total batch-size))) ; генерируем урлы

    (<!! (go-loop []
           (when-let [x (<! out>)]
             (save-xform x)
             (recur))))
    ))

(comment
  (generate-pokemon-urls 55)
  (time (pokemons-types 55 "ja"))
  )
