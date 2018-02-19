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
  (labelfn) will be called and whatever it returns will be used as the label.
  It may also style the label by calling setTheme() or set*groundColour()."
  [labelfn]
  (proxy [com.googlecode.lanterna.gui2.Label] [(labelfn)]
    ; This is an ugly hack. We can't override getText() because it isn't used
    ; during the drawing pass; the renderer accesses the label's internal text
    ; fields directly. Instead we override getThemeDefinition(), which is called
    ; at the start of rendering, and thus any changes it makes to the label's
    ; text will take effect on that rendering pass.
    ; This has the added advantage that the labeler is invoked (just) before the
    ; theme is read, meaning that we can change the label's themes, too.
    (getThemeDefinition []
                        (.setText ^com.googlecode.lanterna.gui2.Label this (labelfn))
                        (proxy-super-cls com.googlecode.lanterna.gui2.Label getThemeDefinition))))
