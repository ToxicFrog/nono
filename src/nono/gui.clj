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

(def doubler {\# "██" \. "  "})
(defn- double-string [string]
  (->> string (map doubler) (apply str)))

(defn- playfield [nonogram]
  (.withBorder
    (->> (nonogram :grid) (map double-string) (string/join \newline) (Label.))
    (Borders/doubleLine)))

(defn- nono-panel [nonogram]
  (lgui/grid-panel 2
                   (statline nonogram)
                   (-> nonogram :cols hints/col-hints)
                   (-> nonogram :rows hints/row-hints)
                   (playfield nonogram)))

(defn run [nonogram]
  (let [gui (lgui/text-gui)
        window (doto (lgui/centered-window "Nonograms")
                 (.setComponent (nono-panel nonogram)))
        ]
    (doto gui
      (.addWindow window)
      (.waitForWindowToClose window))))
