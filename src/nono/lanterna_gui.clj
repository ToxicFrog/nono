(ns nono.lanterna-gui
  "Nono-specific code that has to interface directly with Lanterna."
  (:require [nono.game :as game]
            [lanterna.settings :refer [Colour Theme]]
            [clojure.core.matrix :as mx])
  (:import (com.googlecode.lanterna.gui2 Button Button$FlatButtonRenderer Label)
           ))

; TODO: move to lanterna.widgets
(defn ViewButton
  ([labelfn] (ViewButton labelfn (constantly nil)))
  ([labelfn handler]
     (proxy [Button] [(labelfn) handler]
       (getLabel [] (labelfn))
       (createDefaultRenderer [] (Button$FlatButtonRenderer.)))))

(defn ViewLabel [labelfn]
  (proxy [Label] [(labelfn)]
    ; This is an ugly hack: we know getLabelWidth() is called at the start of
    ; each drawing pass, so we override it to update the label contents first.
    ; Overriding getText() doesn't work because this isn't used in the drawing
    ; pass -- it's just an accessor for internal fields that the renderer
    ; accesses directly.
    (getLabelWidth []
                   (.setText this (labelfn))
                   (proxy-super getLabelWidth))))

(def tiles {:full "██" :empty "╶ " :??? "░░"})
(def next-tile {:??? :full
                :full :empty
                :empty :full})

(defn CellButton
  "Create a button associated with a single cell in the grid."
  [game [row col]]
  (let [labeler (fn [] (tiles (game/get-cell game row col)))
        handler (partial game/update-cell! game row col next-tile)]
    (proxy [Button] [(labeler) handler]
      (createDefaultRenderer [] (Button$FlatButtonRenderer.))
      (getLabel [] (labeler))
      (afterEnterFocus [how from]
                       (game/set-position! game row col)))))
