(ns nono.hints
  "Widget to display the hints for the rows."
  (:require [lanterna.gui :as lgui]
            [lanterna.widgets :refer [Label RightJustifiedRow BottomJustifiedColumn LinearPanel]]
            [lanterna.settings :refer [Theme LinearAlignment]])
  )

(defn- set-width
  "Set the width for the given widget."
  [w widget]
  (doto widget
    (.setPreferredSize
      (-> widget (.getPreferredSize) (.withColumns w)))))

(defn- HintPanel
  "Create a panel of hints oriented in the given direction. Each hint is assumed to be a Panel in its own right. Every even-numbered hint is given a lighter background to make line following easier."
  [dir hints]
  ; Adjust the theme for each even-numbered hint.
  (->> (rest hints)
       (take-nth 2)
       (map #(.setTheme %1 (Theme "#d0d0d0" "#202020")))
       dorun)
  (LinearPanel :direction dir :padding 0 :children hints :align :Fill))

(defn- row-hint
  "Turn the hints for a single row into a panel containing them with 1em spacing between them."
  [row]
  (->> row
       (map #(Label (str %1)))
       (RightJustifiedRow 1)))

(defn row-hints
  "Given the row hints (vec of vec of ints), return a vertical panel containing them."
  [rows]
  (HintPanel :VERTICAL
             (map row-hint rows)))

(defn- col-hint
  "Turn the hints for a single column into a panel containing them, 2em wide, bottom justified."
  [col]
  (->> col
       (map #(Label (format "%2d" %1)))
       (BottomJustifiedColumn)))

(defn col-hints
  "Given the column hints (vec of vec of ints), return a horizontal panel containing them."
  [cols]
  (HintPanel :HORIZONTAL
             (map col-hint cols)))
