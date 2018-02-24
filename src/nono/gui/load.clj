(ns nono.gui.load
  "The window for loading new nonograms.
  Eventually, this will ask where you want to load it from (builtin, disk, or
  download) and return a valid nonogram once the user selects it.
  For now, it just prompts for and returns a nonograms.org puzzle ID."
  (:require [schema.core :as s :refer [def defn fn]]
            [nono.nonogram :as ng]
            [clojure.java.io :as io]
            [clojure.edn :as edn]
            [lanterna.dialogs :as dialogs]))

(defn- get-id
  [text-gui]
  (dialogs/show-number-dialog
    text-gui "Enter nonograms.org puzzle ID" "" ""))

(defn- get-puzzle
  [text-gui id]
  (let [waiting (dialogs/show-waiting-dialog text-gui "Loading"
                                             (str "Downloading puzzle #" id))
        puzzle (ng/from-nonograms-org id)]
    (.close waiting)
    (if puzzle
      puzzle
      (do (dialogs/show-message-dialog
            text-gui "Error"
            (str "Couldn't download puzzle #" id) :OK)
        nil))))

(defn load-by-id :- (s/maybe ng/Nonogram)
  [text-gui :- com.googlecode.lanterna.gui2.WindowBasedTextGUI]
  (some->>
    (get-id text-gui)
    (get-puzzle text-gui)))

; vvvvv
; ★★✫✭☆
; 🕱🕱🕱🕱☠
;‼

(defn- rating [n fg bg]
  (let [count (int (/ n 2))
        pad (- 5 count)]
    (str
      (apply str (repeat count fg))
      (apply str (repeat pad bg)))))

(defn- stars [n] (rating n "★" "☆"))
(defn- bangs [n] (rating n "!" "."))

(defrecord IndexEntry
  [width height title rating difficulty]
  Object
  (toString [this]
            (format "%s %s %3d×%-3d %-32s"
                    (bangs difficulty) (stars rating) width height title))
  Comparable
  (compareTo [this other]
             (compare (* width height)
                      (* (:width other) (:height other)))))

(defn load-by-index :- (s/maybe ng/Nonogram)
  [text-gui :- com.googlecode.lanterna.gui2.WindowBasedTextGUI,
   index-file :- s/Str]
  (let [index (->> index-file
                   io/resource
                   slurp
                   edn/read-string
                   (map map->IndexEntry)
                   sort)
        selected (dialogs/show-selection-list text-gui "Small Puzzles" "" index)]
    (some->> selected :id (get-puzzle text-gui))))
