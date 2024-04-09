(ns game-2048.game
  (:require
    [reagent.core :as reagent :refer [atom]]
    ) )

(def a-grid (atom {:grid  [[nil nil nil nil]
                 [nil nil nil nil]
                 [nil 2 nil nil]
                 [2 nil nil nil]]}))


;; Grid functions.


(defn set-last
  "Sets the last item of the vector to value"
  [vector value]
  (if (= (count vector) 0)
    [value]
    (assoc vector (- (count vector) 1) value)))



(defn pair-compressed-row
  "Creates vector with equal fields paired into subvectors"
  [row]
  (reduce (fn [paired field]
            (if (and (= field (last paired)) (number? (last paired)))
              (set-last paired [field field])
              (conj paired field)))
          []
          row))

(defn merge-compressed-row
  [row]
  "Merge field in compressed row (row without empty fields)"
  (reduce (fn [merged field]
            (if (number? field)
              (conj merged field)
              (let [r (conj merged (reduce + field))]
                ;(println "f" field "r" r)
                r
               )))
          []
          (pair-compressed-row row)))

(defn merge-row
  "Move all field in the row to the left and merge fields with the same value"
  [row]
  (loop [merged-row (merge-compressed-row (filter identity row))]
    (if (< (count merged-row) 4)
      (recur (conj merged-row nil))
      merged-row)))


(defn move-left
  [grid]
  (vec (map merge-row grid)))

(defn move-rigth
  [grid]
  (vec (map (comp vec rseq) (move-left (vec (map (comp vec rseq) grid))))))

(defn move-top
  [grid]
  (vec (apply map vector (move-left (vec (apply map vector grid))))))

(defn move-bottom
  [grid]
  (vec (apply map vector (move-rigth (vec (apply map vector grid))))))



(defn get-empty-coordinates
  [grid]
  (loop [empty-fields []
         y 0]
    (if (= y 4)
      empty-fields
      (recur (into empty-fields (loop [empty-fields []
                                       x 0]
                                  (if (= x 4)
                                    empty-fields
                                    (recur (if (get-in grid [y x])
                                             empty-fields
                                             (conj empty-fields [y x])) (inc x))))) (inc y)))))

(defn insert
  "Inserts 2 or 4 at random free location."
  [grid]
  (let [ place (rand-nth (get-empty-coordinates grid))
        value 2 #_ (rand-nth [2 4])]
    ;(println "new" value "in" place)
    (assoc-in grid place value )))


(defn try-move-with-function
  [grid function]
  (let [moved (function grid)]
    (if (= moved grid)
      grid
      (insert moved))))

(defn try-move
  [grid direction]
  (case direction
    :left (try-move-with-function grid move-left)
    :right (try-move-with-function grid move-rigth)
    :top (try-move-with-function grid move-top)
    :bottom (try-move-with-function grid move-bottom)))

(defn can-move
  [grid]
  (not (= grid
          (try-move grid :left)
          (try-move grid :right)
          (try-move grid :top)
          (try-move grid :bottom))))

(defn move
  "Moves all field to the choosen direction, merges fields and inserts field value for random free location. Returns nil if game is over. Returns same grid if move doesn't change the grid."
  [grid direction]

  (let [
        ;sum1 (apply + (filter some? (flatten grid)))
        res (if (can-move grid)
              (try-move grid direction)
              nil)
        ;sum2 (apply + (filter some? (flatten res)))
        ]
    ;(for [r res]
    ;  (println r))
    ;(println "before sum" sum1 "after" sum2)
    res))


;; Grid test funcions.

(defn random-game
  "Makes as many random moves as possible and returns final grid and number of moves."
  [grid]
  (loop [g grid
         i 0]
    (let [moved (move g (rand-nth [:left :rigth :top :bottom]))]
      (if (or (not moved) (= i 500))
        {:moves i :final-grid g}
        (recur moved (inc i))))))