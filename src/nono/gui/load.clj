(ns nono.gui.load
  "The window for loading new nonograms.
  Eventually, this will ask where you want to load it from (builtin, disk, or
  download) and return a valid nonogram once the user selects it.
  For now, it just prompts for and returns a nonograms.org puzzle ID."
  (:import (com.googlecode.lanterna.gui2.dialogs TextInputDialog)))

(defn load-puzzle [text-gui]
  (TextInputDialog/showNumberDialog
    text-gui
    "Download a puzzle!"
    "nonograms.org puzzle ID"
    ""))
