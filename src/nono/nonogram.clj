(ns nono.nonogram
  "Tools for working with Nonogram data."
  (:require [clojure.string :as string]
            [schema.core :as s :refer [def defn]]))

(def Hint [s/Int])
(def Position [(s/one s/Int "x") (s/one s/Int "y")])
(def CellState (s/enum :empty :full :???))
(def GridCell [(s/one Position "position") (s/one CellState "state")])
(def GridLine [CellState])
(def Grid {Position CellState}) ; should be s/Char
(def Nonogram
  {:title s/Str
   :width s/Int
   :height s/Int
   :grid Grid
   :col-hints [Hint]
   :row-hints [Hint]
   })

(defn rows :- [GridLine]
  [{:keys [grid width height]} :- Nonogram]
  (for [y (range height)]
    (vec (for [x (range width)]
           (grid [x y])))))

(defn cols :- [GridLine]
  [{:keys [grid width height]} :- Nonogram]
  (for [x (range width)]
    (vec (for [y (range height)]
           (grid [x y])))))

(defn cells :- [GridCell]
  "Returns a sequence of cells in row-major order."
  [{:keys [grid width height]} :- Nonogram]
  (for [y (range height) x (range width)]
    [[x y] (grid [x y])]))

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

(defn- ->row
  "Turns a string representing the row at position y into a seq of GridCells."
  [y vals]
  (apply concat (map-indexed #(vector [%1 y] (->cell %2)) vals)))

(defn- ->grid
  "Turn a list of lists into a grid, i.e. a map indexed by [x y] values."
  [lines]
  (apply assoc {}
         (->> lines
              (map-indexed ->row)
              (apply concat))))

(defn- ->nonogram
  "Given a list of lines, turn it into a nonogram. The first line is assumed to
  be the title of the nonogram, subsequent lines are image data where # is filled
  and . is blank.
  Returns a map with the keys
    :title :row-hints :col-hints :width :height :grid
  :grid is a map from [x y] to cell content."
  [lines]
  (let [title (first lines)
        grid (->grid (rest lines))
        height (dec (count lines))
        width (count (last lines))
        nonogram {:title title
                  :grid grid
                  :height (dec (count lines))
                  :width (count (last lines))
                  :col-hints []
                  :row-hints []}
        ]
    (assoc nonogram
      :col-hints (map ->hints (cols nonogram))
      :row-hints (map ->hints (rows nonogram)))))

(defn load [filename] :- Nonogram
  (-> filename slurp string/split-lines ->nonogram))
