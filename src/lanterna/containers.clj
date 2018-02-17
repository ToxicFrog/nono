(ns lanterna.containers
  "Tools for creating and manipulating lanterna Panels, including convenience
  functions for creating them with a specific layout manager and filling them."
  (:require [lanterna.settings :refer [GridAlignment GridLayout
                                       LinearAlignment LinearLayout]])
  (:import (com.googlecode.lanterna.gui2
             BasicWindow Window$Hint Panel Separator Label)))

(defn- add-children
  [container children hints]
  (->> children
       (map #(.addComponent container %1 hints))
       dorun)
  container)

(defn LinearContainer
  [container & {:keys [direction spacing children align]
                :or {spacing 0 children [] align nil}}]
  (add-children
    (doto container
      (.setLayoutManager
        (doto (LinearLayout direction) (.setSpacing spacing))))
    children
    (LinearAlignment align)))

(defn LinearPanel [& args] (apply LinearContainer (Panel.) args))

(defn GridContainer
  [container & {:keys [width margins spacing children align]
                :or {margins 0 spacing 0 children [] align [:FILL :FILL]}}]
  (add-children
    (doto container
      (.setLayoutManager (GridLayout :width width
                                     :margins margins
                                     :spacing spacing)))
    children
    (apply GridAlignment align)))

(defn GridPanel [& args] (apply GridContainer (Panel.) args))

(defn- pad-and-fill-grid
  "Given a BottomJustifiedColumn or RightJustifiedRow, add the necessary padding component to produce the bottom/right justification, then add all the actual children and return the panel."
  [panel children]
  (.addComponent panel (Label. "") (GridAlignment :FILL :FILL true true))
  (->> children
       (map #(.addComponent panel %1 (GridAlignment :FILL :FILL false false)))
       dorun)
  panel)

(defn BottomJustify
  "Equivalent to (LinearContainer parent :direction :VERTICAL :children children)
  except that the children are bottom justified in the container. Uses GridLayout
  internally."
  [parent children & args]
  (pad-and-fill-grid
    (apply GridContainer parent :width 1 args)
    children))

(defn RightJustify
  "Equivalent to (LinearContainer parent :direction :HORIZONTAL :children children)
  except that the children are right justified in the container. Uses GridLayout
  internally."
  [parent children & args]
  (pad-and-fill-grid
    (apply GridContainer parent :width (-> children count inc) args)
    children))

