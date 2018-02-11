(ns nono.core
  (:gen-class)
  (:require [clojure.string :as string]
            [nono.gui :as gui])
  )

(defn- make-clues [line]
  (->> line (partition-by identity) ; split into runs of fills and blanks
       (filter #(= (first %) \#)) ; remove the blanks
       (map count)
       vec))

(defn- cols
  "Given a seq of rows, return a seq of columns instead."
  ; Pass each row into the map as a separate argument, so it ends up calling
  ; vector on all the first elements, then all the second elements, etc.
  [rows] (apply map vector rows))

(defn- ->nono
  "Given a list of lines, turn it into a nonogram. A nonogram has the following keys:
    :map { [row] => line }
    :rows [row clues]
    :cols [col clues]"
  [lines]
  {:title (first lines)
   :grid (rest lines)
   :cols (map make-clues (cols (rest lines)))
   :rows (map make-clues (rest lines))})

(def nono
  (-> "data/16903" slurp string/split-lines ->nono))

(defn -main [& args]
  ; (dorun (map println (nono :cols)))
  ; (dorun (map println (nono :grid)))
  ; (dorun (map println (nono :rows)))
  (gui/run nono)
  )
