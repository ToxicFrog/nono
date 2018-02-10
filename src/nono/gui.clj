(ns nono.gui
  (:require [lanterna.gui :as lgui]
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

(defn row-hint [row]
  (Label. (string/join \space row)))
(defn row-hints [rows]
  (let [panel (doto (Panel.)
                (.setLayoutManager (doto (LinearLayout.)
                                     (.setSpacing 0))))
        sep (lgui/hsep)
        ]
    (.addComponent panel sep)
    (lgui/add-components panel (map row-hint rows))
  ))


(defn col-hint [col]
  (Label. (string/join \newline col)))
(defn col-hints [cols]
  (let [panel (doto (Panel.)
                (.setLayoutManager (doto (LinearLayout. Direction/HORIZONTAL)
                                     (.setSpacing 0)))
                (.addComponent (lgui/vsep)))
        ]
    (lgui/add-components panel (map col-hint cols)))
  )

(defn- nono-panel [nonogram]
  (lgui/grid-panel 2
                   (statline nonogram)
                   (-> nonogram :cols col-hints)
                   (-> nonogram :rows row-hints)
                   (playfield nonogram)))

(defn run [nonogram]
  (let [gui (lgui/text-gui)
        window (doto (lgui/centered-window "Nonograms")
                 (.setComponent (nono-panel nonogram)))
        ]
    (doto gui
      (.addWindow window)
      (.waitForWindowToClose window))))
