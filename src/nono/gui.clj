(ns nono.gui
  (:require [lanterna.gui :as lgui])
  (:import (com.googlecode.lanterna.gui2 Label))
  )

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

(defn- nono-panel []
  (Label. "Test Label"))

(defn run []
  (let [gui (lgui/text-gui)
        window (doto (lgui/fullscreen-window "")
                 (.setComponent (nono-panel)))
        ]
    (doto gui
      (.addWindow window)
      (.waitForWindowToClose window))))
