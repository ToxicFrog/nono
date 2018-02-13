(ns lanterna.gui
  (:require [lanterna.settings :refer [PropertyTheme Colour]])
  (:import (com.googlecode.lanterna.gui2 MultiWindowTextGUI DefaultWindowManager EmptySpace)
           (com.googlecode.lanterna.screen TerminalScreen)
           (com.googlecode.lanterna.terminal DefaultTerminalFactory)))

(def nono-theme (PropertyTheme "data/theme.properties"))

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
