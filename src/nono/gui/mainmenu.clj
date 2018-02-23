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

(defn play-by-id []
  (let [id (load/load-puzzle *text-gui*)
        waiting (show-waiting-dialog *text-gui* "Loading"
                                     (str "Downloading puzzle #" id))
        game (some-> id ng/from-nonograms-org game/create-state)]
    (.close waiting)
    (if game
      (game-ui/play-game *text-gui* game)
      (show-message-dialog *text-gui* "Error"
                           (str "Couldn't download puzzle #" id) :OK))))

(def main-menu-help
  "   Choose a puzzle category to play nonograms.
  Once in-game, press 'h' or '?' to bring up play instructions.")

(def main-menu
  (Window "Nono"
    (ActionList
      ; "Small Puzzles" #(println "small puzzles")
      ; "Medium Puzzles" #(println "medium puzzles")
      ; "Large Puzzles" #(println "large puzzles")
      "Enter Puzzle ID" play-by-id
      "Settings" #(show-message-dialog *text-gui* "Sorry!" "Not implemented yet." :OK)
      "Help" #(show-message-dialog *text-gui* "Help" main-menu-help)
      "Exit" #(.. *text-gui* getActiveWindow close))
    :centered))
