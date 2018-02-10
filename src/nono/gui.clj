(ns nono.gui
  (:require [lanterna.gui :as lgui])
  (:import (com.googlecode.lanterna.gui2 Label))
  )

(defn- nono-panel []
  (lgui/grid-panel 2
                   (lgui/placeholder "status")
                   (lgui/placeholder "cols")
                   (lgui/placeholder "rows")
                   (lgui/placeholder "playfield")))

(defn run []
  (let [gui (lgui/text-gui)
        window (doto (lgui/fullscreen-window "")
                 (.setComponent (nono-panel)))
        ]
    (doto gui
      (.addWindow window)
      (.waitForWindowToClose window))))
