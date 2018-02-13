(ns nono.game
  "Tools for working with the game state."
  (:require [clojure.string :as string]
            [nono.nonogram :as ng]
            [clojure.core.matrix :as mx]
            [schema.core :as s :refer [def defn]]))

; Types!
(def Game
  (s/atom
    {; Cursor position
     :row s/Int
     :col s/Int
     ; Playfield
     :grid ng/Grid
     ; Puzzle definition
     :nonogram ng/Nonogram
     ; Handle to UI
     (s/optional-key :ui) s/Any}))

(defn create-state :- Game
  [nonogram :- ng/Nonogram]
  (atom
    {:row 0 :col 0
     :nonogram nonogram
     :grid (mx/emap (constantly :???) (nonogram :grid))}))

(defn set-position! [game row col]
  (swap! game #(assoc % :row row :col col))
  (.invalidate (-> @game :ui)))

(defn update-cell! [game row col func]
  (swap! game #(update-in % [:grid row col] func)))
(defn set-cell! [game row col val]
  (update-cell! game row col (constantly val)))

(defn get-cell [game row col]
  (mx/mget (@game :grid) row col))
