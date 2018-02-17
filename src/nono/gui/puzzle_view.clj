(ns nono.gui.puzzle-view
  "The grid that contains the puzzle being edited, and event handlers associated
  with it (i.e. basically all of them)."
  (:require [nono.game :as game]
            [schema.core :as s :refer [def defn]]
            [lanterna.widgets :refer [proxy-super-cls]]
            [lanterna.containers :refer [GridPanel]]
            [nono.gui.help :as help]
            [clojure.core.matrix :as mx])
  (:import (com.googlecode.lanterna.gui2 Panel Button Button$FlatButtonRenderer Interactable$Result)
           ))

(def tiles {:full "██" :empty "··" :??? "░░" :mark "¿?"})
(def next-tile {:??? :full
                :full :empty
                :empty :full
                :mark :full})

(def ^:dynamic *text-gui* nil)

(defmulti press-button
  (fn dispatcher [keystroke & args] (.getCharacter keystroke)))

;; Commands for modifying individual cells.
(defmethod press-button \newline [_ game row col]
  (game/update-cell! game row col next-tile)
  true)
(defmethod press-button \space [_ game row col]
  (game/set-cell! game row col :full)
  true)
(defmethod press-button \x [_ game row col]
  (game/set-cell! game row col :empty)
  true)
(defmethod press-button \z [_ game row col]
  (game/set-cell! game row col :???)
  true)
(defmethod press-button \v [_ game row col]
  (game/set-cell! game row col :mark)
  true)

;; Commands for modifying entire rows and columns at a time.
(defmethod press-button \- [_ game row col]
  (game/finish-row! game row :empty)
  true)
(defmethod press-button \\ [_ game row col]
  (game/finish-col! game col :empty)
  true)
(defmethod press-button \_ [_ game row col]
  (game/finish-row! game row :full)
  true)
(defmethod press-button \| [_ game row col]
  (game/finish-col! game col :full)
  true)

;; Metacommands: help and quit.
(defmethod press-button \h [_ game row col]
  (help/show-help *text-gui*)
  true)
(defmethod press-button \? [_ game row col]
  (help/show-help *text-gui*)
  true)
(defmethod press-button \q [_ game row col]
  (.. *text-gui* .getActiveWindow .close)
  true)

;; Fallback; returning false means the caller will super().
(defmethod press-button :default [_ game row col]
  false)

(defn CellButton :- s/Any
  "Create a button associated with a single cell in the grid."
  [game :- game/GameAtom, [row col] :- [s/Int]]
  (let [labeler (fn [] (tiles (game/get-cell game row col)))]
    (proxy [Button] [(labeler)]
      (createDefaultRenderer [] (Button$FlatButtonRenderer.))
      (getLabel [] (labeler))
      (handleKeyStroke [keystroke]
                       (binding [*text-gui* (.getTextGUI this)]
                         (if (press-button keystroke game row col)
                           Interactable$Result/HANDLED
                           (proxy-super-cls Button handleKeyStroke keystroke))))
      (afterEnterFocus [how from]
                       (game/set-position! game row col)))))

(defn puzzle-view :- Panel
  [game :- game/GameAtom]
  (GridPanel
    :width (-> @game :puzzle :width)
    :children (->> @game :puzzle :grid mx/index-seq
                   (map (partial CellButton game)))))
