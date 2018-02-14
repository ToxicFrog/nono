(ns lanterna.settings
  "Constructors for Lanterna setting objects like TextColour, TerminalSize, and LayoutData."
  (:require [clojure.java.io :as io])
  (:import (java.lang Enum)
           (java.util Properties)
    (com.googlecode.lanterna TextColor$Factory TextColor$ANSI TextColor$RGB SGR
                             TerminalSize)
    (com.googlecode.lanterna.graphics SimpleTheme)
    (com.googlecode.lanterna.gui2 GridLayout$Alignment
                                   LinearLayout$Alignment
                                  Direction)
    ))

(defn Dir [dir]
  (Enum/valueOf Direction (name dir)))

(defn Colour
  "Return a TextColour based on the given hex code, or ANSI/DEFAULT if the code is not convertible into a displayable colour."
  ([r g b] (or (TextColor$RGB. r g b) TextColor$ANSI/DEFAULT))
  ([s] (or (TextColor$Factory/fromString s) TextColor$ANSI/DEFAULT)))

(defn Size [w h] (TerminalSize. w h))

(defn PropertyTheme [file]
  (com.googlecode.lanterna.graphics.PropertyTheme.
    (doto (Properties.)
      (.load (io/reader file)))))

(defn Theme [fg bg]
  (SimpleTheme.
    (if (vector? fg) (apply Colour fg) (Colour fg))
    (if (vector? bg) (apply Colour bg) (Colour bg))
    (into-array SGR [])))

(defn LinearAlignment [align]
  (com.googlecode.lanterna.gui2.LinearLayout/createLayoutData
    (Enum/valueOf LinearLayout$Alignment (name align))))

(defn LinearLayout [dir]
  (com.googlecode.lanterna.gui2.LinearLayout. (Dir dir)))

(defn GridAlignment
  ([halign valign]
   (com.googlecode.lanterna.gui2.GridLayout/createLayoutData
     (Enum/valueOf GridLayout$Alignment (name halign))
     (Enum/valueOf GridLayout$Alignment (name valign))))
  ([halign valign hexpand vexpand]
   (com.googlecode.lanterna.gui2.GridLayout/createLayoutData
     (Enum/valueOf GridLayout$Alignment (name halign))
     (Enum/valueOf GridLayout$Alignment (name valign))
     hexpand vexpand)))

(defn GridLayout
  [& {:keys [width margins spacing]
      :or {margins 0 spacing 0}}]
  (doto (com.googlecode.lanterna.gui2.GridLayout. width)
    (.setHorizontalSpacing spacing)
    (.setLeftMarginSize margins)
    (.setRightMarginSize margins)))
