(ns lanterna.gui
  (:import (com.googlecode.lanterna.gui2 DefaultWindowManager EmptySpace
                                         BasicWindow Window$Hint)
           (com.googlecode.lanterna.screen TerminalScreen)
           (com.googlecode.lanterna.terminal DefaultTerminalFactory)))

(def WindowHints
  {"CENTERED" Window$Hint/CENTERED
   "EXPANDED" Window$Hint/EXPANDED
   "FIT_TERMINAL_WINDOW" Window$Hint/FIT_TERMINAL_WINDOW
   "FIXED_POSITION" Window$Hint/FIXED_POSITION
   "FIXED_SIZE" Window$Hint/FIXED_SIZE
   "FULL_SCREEN" Window$Hint/FULL_SCREEN
   "MODAL" Window$Hint/MODAL
   "NO_DECORATIONS" Window$Hint/NO_DECORATIONS
   "NO_FOCUS" Window$Hint/NO_FOCUS
   "NO_POST_RENDERING" Window$Hint/NO_POST_RENDERING
   })

(defn Window
  "Create a BasicWindow with the given child component and, if specified, the given hints (which map to values of the Window$Hint enum, e.g. :CENTERED or :FULL_SCREEN)."
  ([title child]
   (doto (BasicWindow. (str "╼ " title " ╾"))
     (.setComponent child)))
  ([title child & hints]
   (doto (BasicWindow. (str "╼ " title " ╾"))
     (.setComponent child)
     (.setHints (vec (->> hints (map name) (map WindowHints)))))))

(defn MultiWindowTextGUI
  "Create and initialize an empty MultiWindowTextGUI bound to the default terminal."
  [theme]
  (-> (DefaultTerminalFactory.)
      (.createTerminal)
      (doto (.setTitle "Nono"))
      (TerminalScreen.)
      (doto (.startScreen))
      (com.googlecode.lanterna.gui2.MultiWindowTextGUI.
        (DefaultWindowManager.) (EmptySpace.))
      (doto (.setTheme theme))))
