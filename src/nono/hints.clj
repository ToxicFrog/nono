(ns nono.hints
  "Widget to display the hints for the rows."
  (:require [lanterna.gui :as lgui])
  (:import (com.googlecode.lanterna.gui2 Label Borders LinearLayout
                                         LinearLayout$Alignment Panel Direction
                                         EmptySpace GridLayout GridLayout$Alignment))
  )

(def end-alignment
  (LinearLayout/createLayoutData LinearLayout$Alignment/End))
(def fill-alignment
  (LinearLayout/createLayoutData LinearLayout$Alignment/Fill))

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
         (map #(.addComponent panel %1 end-alignment))
         dorun)
    panel))

(defn- line-hint2
  "Create the hints for a single row or col. A line of hints is a Panel containing a line of Labels with spacing between them."
  [dir spacing line]
  (let [panel (doto (Panel.)
                (.setLayoutManager
                  (doto (GridLayout. 1)
                    (.setHorizontalSpacing 0)
                    (.setLeftMarginSize 0)
                    (.setRightMarginSize 0))))
        ]
    (.addComponent panel (Label. "")
                   (GridLayout/createLayoutData
                     GridLayout$Alignment/FILL GridLayout$Alignment/FILL
                     false true))
    (->> line
         (map str)
         (map #(Label. %1))
         (map #(.addComponent panel %1
                              (GridLayout/createHorizontallyEndAlignedLayoutData 1)))
         dorun)
    panel))

(defn row-hints [rows]
  "Create the hints for the rows."
  (let [panel (doto (Panel.)
                (.setLayoutManager
                  (doto (LinearLayout. Direction/VERTICAL) (.setSpacing 0))))
        hints (map (partial line-hint Direction/HORIZONTAL 1) rows)
        ]
    (->> hints
         (take-nth 2)
         (map #(.setTheme %1 (lgui/theme "#d0d0d0" "#202020")))
         dorun)
    (lgui/add-components panel hints fill-alignment)
  ))

(defn- set-width [w widget]
  (doto widget
    (.setPreferredSize
      (-> widget (.getPreferredSize) (.withColumns w)))))

(defn col-hints [cols]
  (let [panel (doto (Panel.)
                (.setLayoutManager
                  (doto (LinearLayout. Direction/HORIZONTAL) (.setSpacing 0))))
        hints (->> cols
                (map (partial line-hint2 Direction/VERTICAL 0))
                (map (partial set-width 2)))
        ]
    (->> hints
         (take-nth 2)
         (map #(.setTheme %1 (lgui/theme "#d0d0d0" "#202020")))
         dorun)
    (lgui/add-components panel hints fill-alignment)
  ))
