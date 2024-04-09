(ns game-2048.core
  (:require
    [reagent.dom :as rd]
    [game-2048.game :refer [a-grid move try-move]]
    [game-2048.events :as ge]))

(enable-console-print!)

(defn move-reset [direction]
  (let [nv (move (:grid @a-grid) direction)]
    (if (not (nil? nv)) (reset! a-grid {:grid nv}) (js/alert "Игра закончена"))
    )
  )

(defn new-game [e]
  (reset! a-grid {:grid [[nil nil nil nil]
                         [nil nil nil nil]
                         [nil 2 nil nil]
                         [2 nil nil nil]]}))

(defn on-arrow-down [e]
  (let [cc (.-keyCode e)]
    (cond
      (= cc 37) (do (move-reset :left))
      (= cc 38) (do (move-reset :top))
      (= cc 39) (do (move-reset :right))
      (= cc 40) (do (move-reset :bottom))
      )))

(defn hello-world [] (fn []
                       [:div
                        {:on-key-down on-arrow-down}
                        [:button {:on-click new-game} "Run or new game"]
                        (ge/render-tile-conteiner)
                        ]
                       ))

(rd/render [hello-world]
           (. js/document (getElementById "app")))

;(defn on-js-reload []
;  ;; optionally touch your app-state to force rerendering depending on
;  ;; your application
;  ;; (swap! app-state update-in [:__figwheel_counter] inc)
;  )
