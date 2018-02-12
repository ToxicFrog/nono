(ns lanterna.settings
  "Constructors for Lanterna setting objects like TextColour, TerminalSize, and LayoutData."
  (:import (java.lang Enum)
    (com.googlecode.lanterna TextColor$Factory TextColor$ANSI SGR
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
  [s] (or (TextColor$Factory/fromString s)
          TextColor$ANSI/DEFAULT))

(defn Size [w h] (TerminalSize. w h))

(defn- as-theme-arg
  "Convert a lexical token into an argument suitable for a ThemeDefinition
  field setter. In practice, this means strings starting with # get wrapped in
  a (Colour ...) call, and vectors get wrapper in (into-array SGR ...).
  Everything else is left alone."
  [token]
  (cond
    (and (string? token) (= \# (first token))) `(Colour ~token)
    (and (vector? token)) `(into-array SGR [])
    :else token))

(defmacro Theme
  "Creates a SimpleTheme with the relevant widgets overridden.
  Overrides have the form:
    (Class fg bg
      (CursorVisible true)
      (Active fg bg [sgr])
      ...)
  String arguments starting with # will be automatically converted into Colours,
  and vector arguments will be automatically converted into SGR arrays."
  [fg bg & overrides]
  (let [theme (gensym "theme")
        make-state-override
        (fn [[state & args]]
          ; Assume the head of the sexpr is a field setter name without the
          ; leading .set, so (Active ...) turns into (.setActive ...)
          `(~(symbol (str ".set" state)) ~@(map as-theme-arg args)))
        make-type-override
        (fn [[type fg bg & state-overrides]]
          ; Each state override ends up as a (doto) clause operating on the
          ; theme definition returned by theme.addOverride(type)
          ; Individual expressions in the (doto) are derived from the
          ; state-overrides.
          `(doto (.addOverride ~theme ~(symbol (str "com.googlecode.lanterna.gui2." type))
                               (Colour ~fg) (Colour ~bg) (into-array SGR []))
             ~@(map make-state-override state-overrides)))
        ]
    ; Turn it into a SimpleTheme. constructor followed by a series of doto
    ; clauses that .addOverride overrides for each type.
    `(let [~theme (SimpleTheme. (Colour ~fg) (Colour ~bg) (into-array SGR []))]
       ~@(map make-type-override overrides)
       ~theme)))

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
