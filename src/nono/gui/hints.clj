(ns nono.gui.hints
  "Widget to display the hints for the rows."
  (:require [lanterna.gui :as lgui]
            [lanterna.widgets :refer [Label]]
            [lanterna.containers :refer [LinearPanel RightJustify BottomJustify]]
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
   ; themes for when the hints are completed.
   ; FG is darkened by 0x60, and BG no longer brightens as much when active.
   [true false false] (Theme [0x60 0x60 0x60] [0x00 0x00 0x00])
   [true false true]  (Theme [0x70 0x70 0x70] [0x20 0x20 0x20])
   [true true false]  (Theme [0x00 0x60 0x60] [0x00 0x20 0x20])
   [true true true]   (Theme [0x00 0x70 0x70] [0x00 0x40 0x40])
   ; themes for victory and defeat
   :victory (Theme [0x00 0xFF 0x80] [0x00 0x00 0x00] :bold)
   :defeat (Theme [0x00 0x00 0x00] [0xFF 0x00 0x00] :bold)
   })

(defn- hint-matches? [game key n]
  (= (get-in @game [:picture :hints key n])
     (get-in @game [:puzzle :hints key n])))

(defn- HintPanel
  "It's like a Panel, but .getThemeDefinition is overloaded to return a Theme
  appropriate to its index, the cursor position, and whether the hints in it
  have been solved yet or not."
  [game key n]
  (proxy [Panel] []
    ; getTheme will eventually be called by children of the panel to figure out
    ; their theme, if they are not themed specifically.
    (getTheme
      [] (cond
           (game/won? game) (themes :victory)
           (game/lost? game) (themes :defeat)
           :else (themes [(hint-matches? game key n) (-> @game key (= n)) (even? n)])))))

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

(defn hint-panel
  [game key]
  (let [dir (key {:col :horizontal :row :vertical})
        row-builder (key {:col ->col-hint :row ->row-hint})]
    (LinearPanel
      :direction dir
      :padding 0
      :align :fill
      :children (map-indexed
                  (partial row-builder game)
                  (-> @game :picture :hints key)))))
