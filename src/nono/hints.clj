(ns nono.hints
  "Widget to display the hints for the rows."
  (:require [lanterna.gui :as lgui]
            [lanterna.widgets :refer [Label RightJustify BottomJustify LinearPanel]]
            [clojure.core.matrix :as mx]
            [lanterna.settings :refer [Theme LinearAlignment]])
  (:import [com.googlecode.lanterna.gui2 Panel])
  )

(defn- set-width
  "Set the width for the given widget."
  [w widget]
  (doto widget
    (.setPreferredSize
      (-> widget (.getPreferredSize) (.withColumns w)))))

; to implement HintPanel, we should override getThemeDefinition in Panel; that
; gets called early in the rendering pass and it can return a theme based on the
; current state of the hintpanel: focused or not, relevant or not.

; ok so we have
; basic theme is b0b0b0/000000
; if it's even-numbered, make it d0d0d0/202020
; if it's active, set r to 0 and then add 004040 to the bg
; if it's finished, halve the fg
(defn- fg-colour [is-active is-complete is-even]
  (cond-> [0xb0 0xb0 0xb0]
      is-even (mx/add [0x10 0x10 0x10])
      is-complete (mx/sub [0x40 0x40 0x40])
      is-active (mx/mul [0 1 1])
      ))

(defn- bg-colour [is-active is-complete is-even]
  (cond-> [0 0 0]
          is-even (mx/add [0x10 0x10 0x10])
          is-active (mx/mul [0 1 1 ])
          is-active (mx/add [0 0x40 0x40])
          ))

; TODO move this into lanterna_gui
(defn- HintPanel
  "It's like a Panel, but .getThemeDefinition is overloaded to return a Theme
  appropriate to its index, the cursor position, and whether the hints in it
  have been solved yet or not."
  [game key n]
  (proxy [Panel] []
    (getThemeDefinition
      [] (.getDefaultDefinition
           (Theme
             (fg-colour (-> @game key (= n))
                        false
                        (even? n))
             (bg-colour (-> @game key (= n))
                        false
                        (even? n)))))))

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
    :children (map-indexed (partial ->row-hint game) (-> @game :nonogram :row-hints))))

(defn col-hints
  "Given the column hints (vec of vec of ints), return a horizontal panel containing them."
  [game]
  (LinearPanel
    :direction :HORIZONTAL
    :padding 0
    :align :Fill
    :children (map-indexed (partial ->col-hint game) (-> @game :nonogram :col-hints))))
