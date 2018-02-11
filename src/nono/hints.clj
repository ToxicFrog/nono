(ns nono.hints
  "Widget to display the hints for the rows."
  (:require [lanterna.gui :as lgui])
  (:import (com.googlecode.lanterna.gui2 Label Borders LinearLayout LinearLayout$Alignment Panel Direction))
  )

(defn- line-hint
  "Create the hints for a single row or col. A line of hints is a Panel containing a line of Labels with spacing between them."
  [dir spacing line]
  (let [panel (doto (Panel.)
                (.setLayoutManager
                  (doto (LinearLayout. dir) (.setSpacing spacing))))
        ]
    (->> line
         (map str)
         (map #(Label. %1))
         (map #(.addComponent panel %1 (LinearLayout/createLayoutData LinearLayout$Alignment/End)))
         dorun)
    panel))

(defn row-hints [rows]
  (let [panel (doto (Panel.)
                (.setLayoutManager
                  (doto (LinearLayout. Direction/VERTICAL) (.setSpacing 0))))
        ]
    (lgui/add-components
      panel (map (partial line-hint Direction/HORIZONTAL 1) rows)
      (LinearLayout/createLayoutData LinearLayout$Alignment/End))
  ))

(defn- set-width [w widget]
  (doto widget
    (.setPreferredSize
      (-> widget (.getPreferredSize) (.withColumns w)))))

(defn col-hints [cols]
  (let [panel (doto (Panel.)
                (.setLayoutManager
                  (doto (LinearLayout. Direction/HORIZONTAL) (.setSpacing 0))))
        ]
    (lgui/add-components
      panel
      (->> cols
                (map (partial line-hint Direction/VERTICAL 0))
                (map (partial set-width 2)))
      (LinearLayout/createLayoutData LinearLayout$Alignment/End))
  ))
