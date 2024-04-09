(ns game-2048.events
  (:require
    [reagent.core :as r]
    [game-2048.game :refer [a-grid]]
    ))

(defn calc-idx [idx]
  (let [i (+ 1 idx)
        i1 (Math/ceil (/ i 4))
        m4 (mod i 4)
        i2 (if (= 0 m4) 4 m4)
        r (str i2 "-" i1)
        ]
    r
    ))

(defn filtered-indexed-grid [grid]
  (let [
        ig (map-indexed (fn [idx itm] {:idx (calc-idx idx) :itm itm}) (flatten grid))
        fg (filter (fn [m] (some? (:itm m))) ig)
        ]
    fg
    ))


(defn gclass [m]
  (str "tile tile-" (:itm m) " tile-position-" (:idx m))
  )

(defn get-v2 [grid]
  (let [fg (filtered-indexed-grid grid)]
    (map-indexed (fn [idx m] (assoc m :class (gclass m) :idx idx)) fg)
    ))

(defn ggrid []
  (:grid @a-grid))

(defn tile-content []
  (let [grid @(r/track ggrid)
        lst (get-v2 grid)
        ]
    (for [c lst]
      [:div {:class (:class c) :key (str "-" (:idx c))}
       [:div.tile-inner {:key (str "--" (:idx c))} (:itm c) ]
       ]
      )
    ))

(defn render-tile-conteiner []
  [:div.tile-container
   (tile-content)
   ]
  )