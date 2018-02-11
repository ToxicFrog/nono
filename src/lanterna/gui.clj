(ns lanterna.gui
  (:require [lanterna.settings :refer [Theme]])
  (:import (com.googlecode.lanterna.gui2 MultiWindowTextGUI DefaultWindowManager EmptySpace)
           (com.googlecode.lanterna.screen TerminalScreen)
           (com.googlecode.lanterna.terminal DefaultTerminalFactory)))

(defn text-gui
  "Create and initialize an empty MultiWindowTextGUI bound to the default terminal."
  []
  (-> (DefaultTerminalFactory.)
      (.createTerminal)
      (doto (.setTitle "Nono"))
      (TerminalScreen.)
      (doto (.startScreen))
      (MultiWindowTextGUI. (DefaultWindowManager.) (EmptySpace.))
      (doto (.setTheme (Theme "#b0b0b0" "#000000"))))
  )
