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
  (let [grid (->> (rest lines) (map vec) (mx/emap ->cell))
        [rows cols] (mx/shape grid)]
    {:title (first lines)
     :grid grid
     :width cols
     :height rows
     :col-hints (map ->hints (mx/columns grid))
     :row-hints (map ->hints (mx/rows grid))}))

(defn load [filename] :- Nonogram
  (-> filename slurp string/split-lines ->nonogram))
