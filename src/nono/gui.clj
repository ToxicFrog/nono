(ns nono.gui
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

; (defn- grid-layout
;   ([grab-extra] (grid-layout grab-extra 1 1))
;   ([grab-extra w h] (GridLayout/createLayoutData
;                        GridLayout$Alignment/FILL GridLayout$Alignment/FILL
;                        grab-extra grab-extra w h))
;   )

; (defn- make-gui []
;   (doto (Panel.)
;     (.setLayoutManager (doto (GridLayout. 2)
;                          (.setHorizontalSpacing 0)
;                          (.setLeftMarginSize 0)
;                          (.setRightMarginSize 0)))
;     (.addComponent (placeholder "#008000" 16 8) (grid-layout false 1 3))
;     (.addComponent (Separator. Direction/VERTICAL) (grid-layout false 1 3))
;     (.addComponent (MapView/MapView game) (grid-layout true))
;     (.addComponent (Separator. Direction/HORIZONTAL) (grid-layout false))
;     (.addComponent (placeholder "#800000" 8 2) (grid-layout false))
;     ))

(defn- colour
  "Return a TextColour based on the given hex code, or ANSI/DEFAULT if the code is not convertible into a displayable colour."
  [s] (or (TextColor$Factory/fromString s)
          TextColor$ANSI/DEFAULT)
  )

; (defn- placeholder [c w h]
;   (EmptySpace. (colour c) (TerminalSize. (int w) (int h))))

(defn- theme
  "Return a SimpleTheme with the given foreground and background colours."
  [fg bg] (SimpleTheme. (colour fg) (colour bg) (into-array SGR []))
  )

(defn- text-gui
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

(defn- nono-panel []
  (Label. "Test Label"))

(defn run []
  (let [gui (text-gui)
        window (doto (BasicWindow. "")
                 (.setHints [Window$Hint/FULL_SCREEN
                             Window$Hint/FIT_TERMINAL_WINDOW
                             Window$Hint/NO_DECORATIONS])
                 (.setComponent (nono-panel)))
        ]
    (doto gui
      (.addWindow window)
      (.waitForWindowToClose window))))
