(ns lanterna.settings
  "Constructors for Lanterna setting objects like TextColour, TerminalSize, and LayoutData."
  (:require [clojure.java.io :as io]
            [clojure.string :as string])
  (:import (java.lang Enum)
           (java.util Properties)
           (com.googlecode.lanterna TextColor$Factory TextColor$ANSI TextColor$RGB SGR TerminalSize)
           (com.googlecode.lanterna.graphics SimpleTheme)
           (com.googlecode.lanterna.gui2 GridLayout$Alignment LinearLayout$Alignment Direction)
           ))

;; Colour and theming.

(defn Colour
  "Return a TextColour based on the given hex code, or ANSI/DEFAULT if the code
  is not convertible into a displayable colour."
  ([r g b] (or (TextColor$RGB. r g b) TextColor$ANSI/DEFAULT))
  ([s] (or (TextColor$Factory/fromString s) TextColor$ANSI/DEFAULT)))

(defn PropertyTheme [file]
  (com.googlecode.lanterna.graphics.PropertyTheme.
    (doto (Properties.)
      (.load (io/reader file)))))

(defn- enumize [type value]
  (Enum/valueOf type (-> value name string/upper-case)))

(defn Theme
  "Create a SimpleTheme with the given foreground and background and (optionally)
  SGRs such as :bold, :underline, etc. fg and bg can be either strings of the
  form '#RRGGBB' or seqs of the form [r g b]. SGRs can be strings or keywords."
  [fg bg & sgr]
  (SimpleTheme.
    (if (vector? fg) (apply Colour fg) (Colour fg))
    (if (vector? bg) (apply Colour bg) (Colour bg))
    (into-array SGR (map (partial enumize SGR) sgr))))

;; Directions and layouts.

(defn Dir
  "Turn :horizontal or :vertical into a Lanterna direction enum."
  [dir] (enumize Direction dir))

(defn Size [w h] (TerminalSize. w h))

(defn LinearAlignment
  "Create a LinearLayout$Alignment with the given alignment preference, one of
  :beginning :center :end :fill."
  [align]
  (com.googlecode.lanterna.gui2.LinearLayout/createLayoutData
    ; can't use enumize here because unlike every other enum this one doesn't
    ; use ALLCAPS
    (Enum/valueOf LinearLayout$Alignment (-> align name string/capitalize))))

(defn LinearLayout [dir]
  (com.googlecode.lanterna.gui2.LinearLayout. (Dir dir)))

(defn GridAlignment
  "Create a GridLayout$Alignment with the given horizontal and vertical alignment
  preference, one of :beginning :center :end :fill."
  ([halign valign]
   (com.googlecode.lanterna.gui2.GridLayout/createLayoutData
     (enumize GridLayout$Alignment halign)
     (enumize GridLayout$Alignment valign)))
  ([halign valign hexpand vexpand]
   (com.googlecode.lanterna.gui2.GridLayout/createLayoutData
     (enumize GridLayout$Alignment halign)
     (enumize GridLayout$Alignment valign)
     hexpand vexpand)))

(defn GridLayout
  "Create a GridLayout with :width columns and optional :margins and :spacing."
  [& {:keys [width margins spacing]
      :or {margins 0 spacing 0}}]
  (doto (com.googlecode.lanterna.gui2.GridLayout. width)
    (.setHorizontalSpacing spacing)
    (.setLeftMarginSize margins)
    (.setRightMarginSize margins)))
