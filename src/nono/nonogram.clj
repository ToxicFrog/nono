(ns nono.nonogram
  "Tools for working with Nonogram data."
  (:require [clojure.string :as string]
            [schema.core :as s :refer [def defn]]))

(def Hint [s/Int])
(def Position [(s/one s/Int "x") (s/one s/Int "y")])
(def GridCell [(s/one Position "position") (s/one s/Any "value")])
(def GridLine [s/Any])
(def Grid {Position s/Any}) ; should be s/Char
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

(defn- ->clues
  "Make the clues for a line by splitting it into runs of adjacent tiles and
  then counting each one. Returns a vector of ints."
  [line]
  (->> line (partition-by identity) ; split into runs of fills and blanks
       (filter #(= (first %) \#)) ; remove the blanks
       (map count)
       vec))

(defn- ->row
  "Turns a single row, at position y, into a seq of [x y], val."
  [y vals]
  (apply concat (map-indexed #(vector [%1 y] %2) vals)))

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
      :col-hints (map ->clues (cols nonogram))
      :row-hints (map ->clues (rows nonogram)))))

(defn load [filename] :- Nonogram
  (-> filename slurp string/split-lines ->nonogram))
