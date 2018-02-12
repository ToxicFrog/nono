(ns lanterna.widgets
  "Simple around common lanterna components. This includes both simple
  constructors, and wrappers that do some configuration or composite together
  multiple widgets."
  (:require [lanterna.settings :refer [Colour GridAlignment GridLayout
                                       Dir LinearAlignment LinearLayout]])
  (:import (com.googlecode.lanterna.gui2
             BasicWindow Window$Hint Panel Separator)))

(defn- add-children
  [container children hints]
  (->> children
       (map #(.addComponent container %1 hints))
       dorun)
  container)

(defn LinearPanel
  [& {:keys [direction spacing children align]
      :or {spacing 0 children [] align nil}}]
  (add-children
    (doto (Panel.)
      (.setLayoutManager
        (doto (LinearLayout direction) (.setSpacing spacing))))
    children
    (LinearAlignment align)))

(defn GridPanel
  [& {:keys [width margins spacing children align]
      :or {margins 0 spacing 0 children [] align [:FILL :FILL]}}]
  (add-children
    (doto (Panel.)
      (.setLayoutManager (GridLayout :width width
                                     :margins margins
                                     :spacing spacing)))
    children
    (apply GridAlignment align)))

(defn Label
  "Create a Label, optionally with specified foreground and background colours."
  ([text] (com.googlecode.lanterna.gui2.Label. text))
  ([text fg] (doto (Label text) (.setForegroundColour (Colour fg))))
  ([text fg bg] (doto (Label text fg) (.setBackgroundColor (Colour bg)))))

(defn VSep [] (Separator. com.googlecode.lanterna.gui2.Direction/VERTICAL))
(defn HSep [] (Separator. com.googlecode.lanterna.gui2.Direction/HORIZONTAL))

(defn Button [text handler]
  (doto (com.googlecode.lanterna.gui2.Button. text handler)
    (.setRenderer (com.googlecode.lanterna.gui2.Button$FlatButtonRenderer.))))

(def WindowHints
  {"CENTERED" Window$Hint/CENTERED
   "EXPANDED" Window$Hint/EXPANDED
   "FIT_TERMINAL_WINDOW" Window$Hint/FIT_TERMINAL_WINDOW
   "FIXED_POSITION" Window$Hint/FIXED_POSITION
   "FIXED_SIZE" Window$Hint/FIXED_SIZE
   "FULL_SCREEN" Window$Hint/FULL_SCREEN
   "MODAL" Window$Hint/MODAL
   "NO_DECORATIONS" Window$Hint/NO_DECORATIONS
   "NO_FOCUS" Window$Hint/NO_FOCUS
   "NO_POST_RENDERING" Window$Hint/NO_POST_RENDERING
   })

(defn Window
  "Create a BasicWindow with the given child component and, if specified, the given hints (which map to values of the Window$Hint enum, e.g. :CENTERED or :FULL_SCREEN)."
  ([title child]
   (doto (BasicWindow. (str "╼ " title " ╾"))
     (.setComponent child)))
  ([title child & hints]
   (doto (BasicWindow. (str "╼ " title " ╾"))
     (.setComponent child)
     (.setHints (vec (->> hints (map name) (map WindowHints)))))))

(defn- pad-and-fill-grid
  "Given a BottomJustifiedColumn or RightJustifiedRow, add the necessary padding component to produce the bottom/right justification, then add all the actual children and return the panel."
  [panel children]
  (.addComponent panel (Label "") (GridAlignment :FILL :FILL true true))
  (->> children
       (map #(.addComponent panel %1 (GridAlignment :FILL :FILL false false)))
       dorun)
  panel)

(defn BottomJustifiedColumn
  "Create a Panel containing the specified children that looks like it uses a vertical LinearLayout, except with the children aligned to the bottom of the Panel."
  [children]
  (pad-and-fill-grid
    (GridPanel :width 1)
    children))

(defn RightJustifiedRow
  "Create a Panel containing the specified children that looks like it uses a horizontal LinearLayout, except with the children aligned to the right of the Panel."
  [spacing children]
  (pad-and-fill-grid
    (GridPanel :width (-> children count inc) :spacing spacing)
    children))

