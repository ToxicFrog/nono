(ns nono.gui.mainmenu
  "The main menu displayed at program startup."
  (:require [nono.gui.help :as help]
            [nono.gui.load :as load]
            [nono.gui.game :as game-ui]
            [nono.game :as game]
            [nono.nonogram :as ng]
            [lanterna.dialogs :refer [show-message-dialog show-waiting-dialog]]
            [lanterna.widgets :refer [ActionList]]
            [lanterna.gui :refer [Window *text-gui*]]))

(defn play-puzzle [puzzle]
  (game-ui/play-game *text-gui*
                     (game/create-state puzzle)))

(defn play-by-id []
  (if-let [puzzle (load/load-by-id *text-gui*)]
    (play-puzzle puzzle)))

(defn play-by-index [index-file]
  (if-let [puzzle (load/load-by-index *text-gui* index-file)]
    (play-puzzle puzzle)))

(def main-menu-help
"Select a puzzle category to select puzzles from a list,
or enter a nonograms.org puzzle ID directly.
Once in game, press 'h' or '?' for controls.")

(def main-menu
  (Window "Nono"
    (ActionList
      "Small Puzzles" #(play-by-index "index/nonograms.org.small")
      "Medium Puzzles" #(play-by-index "index/nonograms.org.small")
      "Large Puzzles" #(play-by-index "index/nonograms.org.small")
      "Enter Puzzle ID" play-by-id
      "Settings" #(show-message-dialog *text-gui* "Sorry!" "Not implemented yet." :OK)
      "Help" #(show-message-dialog *text-gui* "Help" main-menu-help)
      "Exit" #(.. *text-gui* getActiveWindow close))
    :centered))
