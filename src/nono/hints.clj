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
(defn- GridLayoutData [expand-h expand-v]
  (GridLayout/createLayoutData
    GridLayout$Alignment/FILL GridLayout$Alignment/FILL
    expand-h expand-v))

(defn- FillSoutheastGravityLinearGrid
  "Given a BottomJustifiedColumn or RightJustifiedRow, add the necessary padding component to produce the bottom/right justification, then add all the actual children and return the panel."
  [panel children]
  (.addComponent panel (Label. "") (GridLayoutData true true))
  (->> children
       (map #(.addComponent panel %1 (GridLayoutData false false)))
       dorun)
  panel)

(defn- BottomJustifiedColumn
  "Create a Panel containing the specified children that looks like it uses a vertical LinearLayout, except with the children aligned to the bottom of the Panel."
  [children]
  (FillSoutheastGravityLinearGrid
    (doto (Panel.) (.setLayoutManager (lgui/dense-grid 1)))
    children))

(defn- RightJustifiedRow [spacing children]
  "Create a Panel containing the specified children that looks like it uses a horizontal LinearLayout, except with the children aligned to the right of the Panel."
  (FillSoutheastGravityLinearGrid
    (doto (Panel.)
      (.setLayoutManager
        (-> children count inc
            lgui/dense-grid
            (.setHorizontalSpacing spacing))))
    children))

(defn- linear-panel
  "Return a Panel with a LinearLayout in the specified direction."
  [dir]
  (doto (Panel.)
    (.setLayoutManager
      (doto (LinearLayout. dir) (.setSpacing 0)))))

(defn- set-width
  "Set the width for the given widget."
  [w widget]
  (doto widget
    (.setPreferredSize
      (-> widget (.getPreferredSize) (.withColumns w)))))

(defn- HintPanel
  "Create a panel of hints oriented in the given direction. Each hint is assumed to be a Panel in its own right. Every even-numbered hint is given a lighter background to make line following easier."
  [dir hints]
  (let [panel (linear-panel dir)
        ]
    (->> (rest hints)
         (take-nth 2)
         (map #(.setTheme %1 (lgui/theme "#d0d0d0" "#202020")))
         dorun)
    (lgui/add-components panel hints fill-alignment)
    ))

(defn- row-hint
  "Turn the hints for a single row into a panel containing them with 1em spacing between them."
  [row]
  (->> row
       (map #(Label. (str %1)))
       (RightJustifiedRow 1)))

(defn row-hints
  "Given the row hints (vec of vec of ints), return a vertical panel containing them."
  [rows]
  (HintPanel Direction/VERTICAL
             (map row-hint rows)))

(defn- col-hint
  "Turn the hints for a single column into a panel containing them, 2em wide, bottom justified."
  [col]
  (->> col
       (map #(Label. (format "%2d" %1)))
       (BottomJustifiedColumn)))

(defn col-hints
  "Given the column hints (vec of vec of ints), return a horizontal panel containing them."
  [cols]
  (HintPanel Direction/HORIZONTAL
             (map col-hint cols)))
