(ns nono.nonogram
  "Tools for working with Nonogram data."
  (:require [clojure.string :as string]))

(defn rows [{:keys [grid width height]}]
  (for [y (range height)]
    (vec (for [x (range width)]
           (grid [x y])))))

(defn cols [{:keys [grid width height]}]
  (for [x (range width)]
    (vec (for [y (range height)]
           (grid [x y])))))

(defn cells
  "Returns a sequence of cells in row-major order."
  [{:keys [grid width height]}]
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
                  :width (count (last lines))}
        ]
    (assoc nonogram
      :col-hints (map ->clues (cols nonogram))
      :row-hints (map ->clues (rows nonogram)))))

(defn load [filename]
  (-> filename slurp string/split-lines ->nonogram))
