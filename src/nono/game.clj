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
   ; Number of mistakes made, in game modes that support that.
   :errors s/Int
   ; The thing the user edits to try to match the target picture.
   :puzzle ng/Nonogram
   ; The target picture the user is trying to match.
   :picture ng/Nonogram})
(def GameAtom (s/atom Game))

; This is necessary because mx/fill returns an NDWrapper rather than whatever
; type you passed into it; see https://github.com/mikera/core.matrix/issues/333
(defn- fill [matrix value]
  (-> matrix (mx/fill value) (mx/matrix)))

(defn- copy-and-clear [nonogram]
  (assoc nonogram
    :grid (fill (nonogram :grid) :???)
    :hints {:row (fill (-> nonogram :hints :row) 0)
            :col (fill (-> nonogram :hints :col) 0)}))

(defn create-state :- GameAtom
  [nonogram :- ng/Nonogram]
  (atom
    {:row 0 :col 0 :errors 0
     :picture nonogram
     :puzzle (copy-and-clear nonogram)}
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

(defn- map-puzzle [func game]
  (update game :puzzle (partial ng/map-grid func)))

(defn finish-row! :- Game
  [game :- GameAtom, row :- s/Int, state :- ng/CellState]
  (swap! game
    (partial map-puzzle
             (fn [[r c] val]
               (if (and (= r row) (#{:mark :???} val))
                 state val)))))

(defn finish-col! :- Game
  [game :- GameAtom, col :- s/Int, state :- ng/CellState]
  (swap! game
    (partial map-puzzle
             (fn [[r c] val]
               (if (and (= c col) (#{:mark :???} val))
                 state val)))))

(defn won? :- s/Bool
  "True if the game has been won."
  [game :- GameAtom]
  (= (-> @game :puzzle :grid) (-> @game :picture :grid)))

(defn lost? :- s/Bool
  "True if the game has been lost (e.g. in modes where you have a limited number
  of mistakes permitted). Not yet implemented."
  [game :- GameAtom]
  false)
