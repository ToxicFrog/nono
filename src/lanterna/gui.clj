(ns lanterna.gui
  (:import (com.googlecode.lanterna TerminalSize TerminalPosition TextColor TextCharacter TextColor$Factory TextColor$ANSI SGR)
           (com.googlecode.lanterna.gui2 Panel GridLayout Label TextBox EmptySpace Button BasicWindow
                                         MultiWindowTextGUI DefaultWindowManager Borders
                                         FatWindowDecorationRenderer Panels Component AbstractComponent
                                         LinearLayout Direction GridLayout$Alignment
                                         LinearLayout$Alignment
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

(defn size [w h] (TerminalSize. w h))

(defn placeholder
  ([text] (Label. text))
  ([text fg bg]
   (doto (Label. text)
     (.setBackgroundColor (colour fg))
     (.setForegroundColour (colour bg)))))

(defn vsep [] (Separator. Direction/VERTICAL))
(defn hsep [] (Separator. Direction/HORIZONTAL))

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

(defn centered-window
  "Returns an empty BasicWindow with decorations, centered."
  [title]
  (doto (BasicWindow. (str \╼ title \╾))
    (.setHints [Window$Hint/CENTERED])))

(defn add-components
  ([container components]
   (reduce
     #(doto %1 (.addComponent %2))
     container components))
  ([container components hints]
   (reduce
     #(doto %1 (.addComponent %2 hints))
     container components))
  )

(defn dense-grid [w]
  (doto (Panel.)
    (.setLayoutManager
      (doto (GridLayout. w)
        (.setHorizontalSpacing 0)
        (.setLeftMarginSize 0)
        (.setRightMarginSize 0)))))

(defn grid-panel
  "Return a Panel containing a Wx? GridLayout and the given components."
  [w & components]
  (let [panel (dense-grid w)
        ]
    (add-components panel components
                    (GridLayout/createLayoutData GridLayout$Alignment/FILL GridLayout$Alignment/FILL))))
