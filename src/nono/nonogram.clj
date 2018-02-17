(ns nono.nonogram
  "Tools for working with Nonogram data."
  (:require [clojure.string :as string]
            [clojure.core.matrix :as mx]
            [schema.core :as s :refer [def defn fn]]))

; Types!
(def Hint [s/Int])
(def Position
  "Position of a cell in the grid. 0-origin, northwest gravity."
  [(s/one s/Int "row") (s/one s/Int "col")])
(def CellState (s/enum :empty :full :??? :mark))
(def GridSlice [CellState])
(def Grid [GridSlice])
(def Nonogram
  {:title s/Str
   :width s/Int
   :height s/Int
   :grid Grid
   :hints {:row (s/constrained [Hint] associative?)
           :col (s/constrained [Hint] associative?)}
   })

(defn row :- GridSlice
  [nonogram :- Nonogram, row :- s/Int]
  (mx/select (nonogram :grid) row :all))

(defn col :- GridSlice
  [nonogram :- Nonogram, col :- s/Int]
  (mx/select (nonogram :grid) :all col))

(def char->cell {\. :empty \# :full})

(defn- slice->hints
  "Make the hints for a line by splitting it into runs of adjacent tiles and
  then counting each one. Returns a vector of ints."
  [line]
  (->> line (partition-by identity) ; split into runs of fills and blanks
       (filter #(= (first %) :full)) ; remove the blanks
       (map count)
       vec))

(defn- cell->hints
  "Recalculate the hints for a given row and column, and return a version of the
  nonogram with the new hints."
  [ng r c]
  (-> ng
      (assoc-in [:hints :row r]
                (slice->hints (row ng r)))
      (assoc-in [:hints :col c]
                (slice->hints (col ng c)))))

(defn- lines->nonogram
  "Given a list of lines, turn it into a nonogram. The first line is assumed to
  be the title of the nonogram, subsequent lines are image data where # is filled
  and . is blank."
  [lines]
  (let [grid (->> (rest lines) (map vec) (mx/emap char->cell))
        [rows cols] (mx/shape grid)]
    {:title (first lines)
     :grid grid
     :width cols
     :height rows
     :hints {:col (mapv slice->hints (mx/columns grid))
             :row (mapv slice->hints (mx/rows grid))}}))

(defn str->nonogram :- Nonogram
  [pattern :- s/Str]
  (-> pattern string/split-lines lines->nonogram))

(defn file->nonogram :- Nonogram
  [filename :- s/Str]
  (-> filename slurp string/split-lines lines->nonogram))

(defn update-cell :- Nonogram
  [ng :- Nonogram, row :- s/Int, col :- s/Int, update :- (s/=> CellState CellState)]
  (-> ng
      (update-in [:grid row col] update)
      (cell->hints row col)))
