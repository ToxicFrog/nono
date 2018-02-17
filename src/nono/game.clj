(ns nono.game
  "Tools for working with the game state."
  (:require [clojure.string :as string]
            [nono.nonogram :as ng]
            [clojure.core.matrix :as mx]
            [schema.core :as s :refer [def defn]]))

; Types!
(def Game
  {; Cursor position
   :row s/Int
   :col s/Int
   ; The thing the user edits to try to match the target picture.
   :puzzle ng/Nonogram
   ; The target picture the user is trying to match.
   :picture ng/Nonogram})
(def GameAtom (s/atom Game))

(defn create-state :- GameAtom
  [nonogram :- ng/Nonogram]
  (atom
    {:row 0 :col 0
     :picture nonogram
     :puzzle (update nonogram :grid (partial mx/emap (constantly :???)))}
    :validator (partial s/validate Game)))

(defn set-position! [game row col]
  (swap! game #(assoc % :row row :col col)))

(defn update-cell! [game row col func]
  (swap! game (fn [game]
                (assoc game :puzzle
                  (ng/update-cell (game :puzzle) row col func)))))

(defn set-cell! [game row col val]
  (update-cell! game row col (constantly val)))

(defn get-cell :- ng/CellState
  [game :- GameAtom, row :- s/Int, col :- s/Int]
  (mx/mget (-> @game :puzzle :grid) row col))

(defn row :- ng/GridSlice
  [game :- GameAtom, row :- s/Int]
  (mx/select (-> @game :puzzle :grid) row :all))

(defn col :- ng/GridSlice
  [game :- GameAtom, col :- s/Int]
  (mx/select (-> @game :puzzle :grid) :all col))
