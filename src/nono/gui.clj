(ns nono.gui
  (:require [lanterna.gui :as lgui]
            [nono.hints :as hints]
            [clojure.string :as string])
  (:import (com.googlecode.lanterna.gui2 Label Borders LinearLayout Panel Direction))
  )

(defn- statline [nonogram]
  (Label. (str
            (nonogram :title) \newline
            (count (nonogram :cols)) \× (count (nonogram :rows)) \newline
            )))

(def doubler {\# "██" \. "╺╸" \X "░░"})
(defn- double-string [string]
  (->> string (map doubler) (apply str)))

(defn- playfield [nonogram]
  (->> (nonogram :grid) (map double-string) (string/join \newline) (Label.)))

(defn- nono-panel [nonogram]
  (lgui/grid-panel 3
                   (statline nonogram)
                   (lgui/vsep)
                   (-> nonogram :cols hints/col-hints)

                   (lgui/hsep) (Label. "┼") (lgui/hsep)

                   (-> nonogram :rows hints/row-hints)
                   (lgui/vsep)
                   (playfield nonogram)))

(defn run [nonogram]
  (let [window (doto (lgui/centered-window "Nonograms")
                 (.setComponent (nono-panel nonogram)))
        ]
    (doto (lgui/text-gui)
      (.addWindow window)
      (.waitForWindowToClose window))))
