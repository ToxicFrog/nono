(ns nono.nonogram
  "Tools for working with Nonogram data."
  (:require [clojure.string :as string]
            [clojure.core.matrix :as mx]
            [schema.core :as s :refer [def defn fn]]))

; Types!
(def Hint [s/Int])
(def Position
  "Position of a cell in the grid. 0-origin, northwest gravity."
  [(s/one s/Int "x") (s/one s/Int "y")])
(def CellState (s/enum :empty :full :???))
(def GridCell {:position Position :state CellState})
(def GridLine [CellState])
(def Grid [GridLine])
(def Nonogram
  {:title s/Str
   :width s/Int
   :height s/Int
   :grid Grid
   :col-hints [Hint]
   :row-hints [Hint]
   })

(defn rows :- [GridLine]
  [{:keys [grid]} :- Nonogram]
  (mx/rows grid))

(defn cols :- [GridLine]
  [{:keys [grid]} :- Nonogram]
  (mx/columns grid))

(defn cells :- [GridCell]
  "Returns a sequence of cells in row-major order."
  [{:keys [grid]} :- Nonogram]
  (->> grid
       (mx/emap-indexed
         (fn :- GridCell [pos :- Position, state :- CellState]
           {:position pos :state state}))
       (mx/transpose)  ; convert from col-major to row-major
       (mx/eseq)))

(defn- ->cell [char]
  ({\. :empty \# :full} char))

(defn- ->hints
  "Make the hints for a line by splitting it into runs of adjacent tiles and
  then counting each one. Returns a vector of ints."
  [line]
  (->> line (partition-by identity) ; split into runs of fills and blanks
       (filter #(= (first %) :full)) ; remove the blanks
       (map count)
       vec))

(defn- ->nonogram
  "Given a list of lines, turn it into a nonogram. The first line is assumed to
  be the title of the nonogram, subsequent lines are image data where # is filled
  and . is blank."
  [lines]
  (let [title (first lines)
        ; transpose it at the end because core.matrix uses column-major order
        grid (->> (rest lines) (map vec) (mx/emap ->cell) mx/transpose)
        [width height] (mx/shape grid)
        nonogram {:title title
                  :grid grid
                  :width width
                  :height height
                  :col-hints []
                  :row-hints []}
        ]
    (assoc nonogram
      :col-hints (map ->hints (cols nonogram))
      :row-hints (map ->hints (rows nonogram)))))

(defn load [filename] :- Nonogram
  (-> filename slurp string/split-lines ->nonogram))
