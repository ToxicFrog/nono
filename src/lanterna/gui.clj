(ns lanterna.gui
  (:require [lanterna.settings :refer [Theme Colour]])
  (:import (com.googlecode.lanterna.gui2 MultiWindowTextGUI DefaultWindowManager EmptySpace)
           (com.googlecode.lanterna.screen TerminalScreen)
           (com.googlecode.lanterna.terminal DefaultTerminalFactory)))

(def nono-theme
  (Theme
    "#b0b0b0" "#000000"
    (Button "#b0b0b0" "#000000"
            (CursorVisible false)
            (Active "#00d0d0" "#004444" [])
            (Selected "#00d0d0" "#004444" []))))

(defn text-gui
  "Create and initialize an empty MultiWindowTextGUI bound to the default terminal."
  []
  (-> (DefaultTerminalFactory.)
      (.createTerminal)
      (doto (.setTitle "Nono"))
      (TerminalScreen.)
      (doto (.startScreen))
      (MultiWindowTextGUI. (DefaultWindowManager.) (EmptySpace.))
      (doto (.setTheme nono-theme)))
  )
