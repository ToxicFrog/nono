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
     :x s/Int
     :y s/Int
     ; Playfield
     :grid ng/Grid
     ; Puzzle definition
     :nonogram ng/Nonogram}))

(defn create-state :- Game
  [nonogram :- ng/Nonogram]
  (atom
    {:x 0 :y 0
     :nonogram nonogram
     :grid (mx/emap (constantly :???) (nonogram :grid))}))
