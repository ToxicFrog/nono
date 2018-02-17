(ns nono.hints
  "Widget to display the hints for the rows."
  (:require [lanterna.gui :as lgui]
            [lanterna.widgets :refer [Label RightJustify BottomJustify LinearPanel]]
            [clojure.core.matrix :as mx]
            [nono.game :as game]
            [nono.nonogram :as ng]
            [lanterna.settings :refer [Theme LinearAlignment]])
  (:import [com.googlecode.lanterna.gui2 Panel])
  )


(def themes
  ; complete? active? even?
  {[false false false] (Theme [0xb0 0xb0 0xb0] [0x00 0x00 0x00])
   [false false true]  (Theme [0xc0 0xc0 0xc0] [0x20 0x20 0x20])
   [false true false]  (Theme [0x00 0xb0 0xb0] [0x00 0x30 0x30])
   [false true true]   (Theme [0x00 0xc0 0xc0] [0x00 0x50 0x50])
   ; TODO: reduce fg by 0x40 across the board when complete
   })

; TODO move this into lanterna_gui
(defn- HintPanel
  "It's like a Panel, but .getThemeDefinition is overloaded to return a Theme
  appropriate to its index, the cursor position, and whether the hints in it
  have been solved yet or not."
  [game key n]
  (proxy [Panel] []
    ; getTheme will eventually be called by children of the panel to figure out
    ; their theme, if they are not themed specifically.
    (getTheme
      [] (themes [false (-> @game key (= n)) (even? n)]))))

(defn- ->row-hint
  "Turn the hints for a single row into a panel containing them with 1em spacing between them."
  [game n hints]
  (RightJustify
    (HintPanel game :row n)
    (map #(Label (str %1)) hints)
    :spacing 1))

(defn- ->col-hint
  "Turn the hints for a single column into a panel containing them, 2em wide, bottom justified."
  [game n hints]
  (BottomJustify
    (HintPanel game :col n)
    (map #(Label (format "%2d" %1)) hints)))

(defn row-hints
  "Given the row hints (vec of vec of ints), return a vertical panel containing them."
  [game]
  (LinearPanel
    :direction :VERTICAL
    :padding 0
    :align :Fill
    :children (map-indexed (partial ->row-hint game) (-> @game :picture :hints :row))))

(defn col-hints
  "Given the column hints (vec of vec of ints), return a horizontal panel containing them."
  [game]
  (LinearPanel
    :direction :HORIZONTAL
    :padding 0
    :align :Fill
    :children (map-indexed (partial ->col-hint game) (-> @game :picture :hints :col))))
