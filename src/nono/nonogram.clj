(ns nono.nonogram
  "Tools for working with Nonogram data."
  (:require [clojure.string :as string]
            [clojure.core.matrix :as mx]
            [clojure.data.json :as json]
            [clojure.java.io :as io]
            [nono.downloader :as downloader]
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

;; Accessors
(defn row :- GridSlice
  [nonogram :- Nonogram, row :- s/Int]
  (mx/select (nonogram :grid) row :all))

(defn col :- GridSlice
  [nonogram :- Nonogram, col :- s/Int]
  (mx/select (nonogram :grid) :all col))

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

(defn update-cell :- Nonogram
  [ng :- Nonogram, row :- s/Int, col :- s/Int, update :- (s/=> CellState CellState)]
  (-> ng
      (update-in [:grid row col] update)
      (cell->hints row col)))

(defn map-grid :- Nonogram
  [func :- (s/=> [[s/Int s/Int] CellState] CellState), ng :- Nonogram]
  (update ng :grid (partial mx/emap-indexed func)))

;; Stuff for loading nonograms
(def char->cell {\. :empty \# :full})

(defn- from-hints
  [title row-hints col-hints]
  (let [width (count col-hints)
        height (count row-hints)]
    {:title title
     :width width
     :height height
     :hints {:col col-hints :row row-hints}
     :grid (mx/matrix (mx/broadcast :??? [height width]))}))

(defn from-json :- Nonogram
  [text :- s/Str]
  (let [value (json/read-str text :key-fn keyword)]
    ; TODO: support nonogram definitions that define the grid rather than (or
    ; as well as) the hints.
    (from-hints
      (value :title "(untitled)")
      (value :rows)
      (value :columns))))

(defn from-resource :- Nonogram
  [path]
  (-> path io/resource slurp from-json))

(defn from-file :- Nonogram
  [path]
  (-> path slurp from-json))

(defn from-nonograms-org :- (s/maybe Nonogram)
  [id :- s/Int]
  (if-let [puzzle (downloader/get-puzzle id)]
    (let [[title grid] puzzle
          [height width] (mx/shape grid)
          grid (mx/emap [:empty :full] grid)]
      {:title title
       :width width
       :height height
       :grid grid
       :hints {:row (mapv slice->hints (mx/rows grid))
               :col (mapv slice->hints (mx/columns grid))}})
    nil))
