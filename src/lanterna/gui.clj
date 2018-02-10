(ns lanterna.gui
  (:import (com.googlecode.lanterna TerminalSize TerminalPosition TextColor TextCharacter TextColor$Factory TextColor$ANSI SGR)
           (com.googlecode.lanterna.gui2 Panel GridLayout Label TextBox EmptySpace Button BasicWindow
                                         MultiWindowTextGUI DefaultWindowManager Borders
                                         FatWindowDecorationRenderer Panels Component AbstractComponent
                                         LinearLayout Direction GridLayout$Alignment
                                         BorderLayout BorderLayout$Location
                                         EmptyWindowDecorationRenderer AbstractWindow Window$Hint
                                         ComponentRenderer AbstractComponent Separator TextBox)
           (com.googlecode.lanterna.screen Screen TerminalScreen)
           (com.googlecode.lanterna.terminal DefaultTerminalFactory Terminal)
           (com.googlecode.lanterna.graphics SimpleTheme BasicTextImage)))

(defn colour
  "Return a TextColour based on the given hex code, or ANSI/DEFAULT if the code is not convertible into a displayable colour."
  [s] (or (TextColor$Factory/fromString s)
          TextColor$ANSI/DEFAULT)
  )

; (defn- placeholder [c w h]
;   (EmptySpace. (colour c) (TerminalSize. (int w) (int h))))

(defn theme
  "Return a SimpleTheme with the given foreground and background colours."
  [fg bg] (SimpleTheme. (colour fg) (colour bg) (into-array SGR []))
  )

(defn text-gui
  "Create and initialize an empty MultiWindowTextGUI bound to the default terminal."
  []
  (-> (DefaultTerminalFactory.)
      (.createTerminal)
      (doto (.setTitle "Nono"))
      (TerminalScreen.)
      (doto (.startScreen))
      (MultiWindowTextGUI. (DefaultWindowManager.) (EmptySpace.))
      (doto (.setTheme (theme "#b0b0b0" "#000000"))))
  )

(defn fullscreen-window
  "Returns an empty BasicWindow with no decorations that fills the screen."
  [title]
  (doto (BasicWindow. title)
    (.setHints [Window$Hint/FULL_SCREEN
                Window$Hint/FIT_TERMINAL_WINDOW
                Window$Hint/NO_DECORATIONS])))
