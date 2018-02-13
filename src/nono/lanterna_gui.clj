(ns nono.lanterna-gui
  "Nono-specific code that has to interface directly with Lanterna."
  (:require [nono.game :refer [get-cell set-position! update-cell!]]
            [lanterna.settings :refer [Colour Theme]]
            [clojure.core.matrix :as mx])
  (:import (com.googlecode.lanterna.gui2 Button Button$FlatButtonRenderer Label)
           (com.googlecode.lanterna SGR)))

; TODO: move to lanterna.widgets
(defn ViewButton
  ([labelfn] (ViewButton labelfn (constantly nil)))
  ([labelfn handler]
     (proxy [Button] [(labelfn) handler]
       (getLabel [] (labelfn))
       (createDefaultRenderer [] (Button$FlatButtonRenderer.)))))

(defn ViewLabel [labelfn]
  (proxy [Label] [(labelfn)]
    (getText [] (println "Label gettext: " (labelfn)) (labelfn))
    (isInvalid [] true)))
