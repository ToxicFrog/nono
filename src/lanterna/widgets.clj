(ns lanterna.widgets
  "Wrappers around simple lanterna widgets such as separators, buttons, and labels."
  (:require [lanterna.settings :refer [Colour GridAlignment GridLayout
                                       Dir LinearAlignment LinearLayout]])
  (:import (com.googlecode.lanterna.gui2
             BasicWindow Window$Hint Panel Separator
             Button$FlatButtonRenderer)))

(defn VSep [] (Separator. com.googlecode.lanterna.gui2.Direction/VERTICAL))
(defn HSep [] (Separator. com.googlecode.lanterna.gui2.Direction/HORIZONTAL))

(defn Button [text handler]
  (doto (com.googlecode.lanterna.gui2.Button. text handler)
    (.setRenderer (com.googlecode.lanterna.gui2.Button$FlatButtonRenderer.))))

(defn ViewButton
  "Create a Button with a dynamically updating label. On each rendering pass,
  (labelfn) will be called and whatever it returns will be used as the label."
  ([labelfn] (ViewButton labelfn (constantly nil)))
  ([labelfn handler]
     (proxy [com.googlecode.lanterna.gui2.Button] [(labelfn) handler]
       (getLabel [] (labelfn))
       (createDefaultRenderer [] (Button$FlatButtonRenderer.)))))

(defn Label
  "Create a Label, optionally with specified foreground and background colours."
  ([text] (com.googlecode.lanterna.gui2.Label. text))
  ([text fg] (doto (Label text) (.setForegroundColour (Colour fg))))
  ([text fg bg] (doto (Label text fg) (.setBackgroundColor (Colour bg)))))

(defmacro proxy-super-cls [cls meth & args]
  (let [thissym (with-meta (gensym) {:tag cls})]
    `(let [~thissym ~'this]
      (proxy-call-with-super (fn [] (. ~thissym ~meth ~@args)) ~thissym ~(name meth))
    )))

(defn ViewLabel
  "Create a Label with a dynamically updating label. On each rendering pass,
  (labelfn) will be called and whatever it returns will be used as the label."
  [labelfn]
  (proxy [com.googlecode.lanterna.gui2.Label] [(labelfn)]
    ; This is an ugly hack: we know getLabelWidth() is called at the start of
    ; each drawing pass, so we override it to update the label contents first.
    ; Overriding getText() doesn't work because this isn't used in the drawing
    ; pass -- it's just an accessor for internal fields that the renderer
    ; accesses directly.
    (getLabelWidth []
                   (.setText ^com.googlecode.lanterna.gui2.Label this (labelfn))
                   (proxy-super-cls com.googlecode.lanterna.gui2.Label getLabelWidth))))
