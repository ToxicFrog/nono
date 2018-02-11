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

(defn- BottomJustifiedColumn [children]
  (let [panel (doto (Panel.)
                (.setLayoutManager (lgui/dense-grid 1)))
        ]
    (.addComponent panel (Label. "")
                   (GridLayout/createLayoutData
                     GridLayout$Alignment/FILL GridLayout$Alignment/FILL
                     true true))
    (->> children
         (map #(.addComponent panel %1
                              (GridLayout/createHorizontallyEndAlignedLayoutData 1)))
         dorun)
    panel))

(defn- RightJustifiedRow [spacing children]
  (let [panel (doto (Panel.)
                (.setLayoutManager (-> children count inc lgui/dense-grid (.setHorizontalSpacing spacing))))
        ]
    (.addComponent panel (Label. "")
                   (GridLayout/createLayoutData
                     GridLayout$Alignment/FILL GridLayout$Alignment/FILL
                     true true))
    (->> children
         (map #(.addComponent panel %1
                              (GridLayout/createLayoutData
                                GridLayout$Alignment/FILL GridLayout$Alignment/FILL
                                false false)))
         dorun)
    panel))

(defn- linear-panel [dir]
  (doto (Panel.)
    (.setLayoutManager
      (doto (LinearLayout. dir) (.setSpacing 0)))))

(defn- set-width [w widget]
  (doto widget
    (.setPreferredSize
      (-> widget (.getPreferredSize) (.withColumns w)))))

(defn- create-hints [dir hints]
  (let [panel (linear-panel dir)
        ]
    (->> (rest hints)
         (take-nth 2)
         (map #(.setTheme %1 (lgui/theme "#d0d0d0" "#202020")))
         dorun)
    (lgui/add-components panel hints fill-alignment)
    ))

(defn row-hint [row]
  (->> row
       (map #(Label. (str %1)))
       (RightJustifiedRow 1)))

(defn row-hints [rows]
  "Create the hints for the rows."
  (create-hints Direction/VERTICAL
                (map row-hint rows)))

(defn col-hint [col]
  (->> col
       (map #(Label. (format "%2d" %1)))
       ;(map #(set-width 2 %1))
       (BottomJustifiedColumn)))

(defn col-hints [cols]
  (create-hints Direction/HORIZONTAL
                (map col-hint cols)))
