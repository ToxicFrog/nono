(ns nono.gui
  (:require [lanterna.widgets :refer [Window VSep HSep Label GridPanel]]
            [lanterna.gui :as lgui]
            [nono.hints :as hints]
            [clojure.string :as string])
  )

(defn- statline [nonogram]
  (Label (str
          ; (nonogram :title) \newline
          (count (nonogram :cols)) \× (count (nonogram :rows)) \newline
          )))

(def doubler {\# "██" \. "╶╴"})
(defn- double-string [string]
  (->> string (map doubler) (apply str)))

(defn- playfield [nonogram]
  (->> (nonogram :grid) (map double-string) (string/join \newline) Label))

(defn- nono-panel [nonogram]
  (GridPanel
    :width 3
    :children [(statline nonogram) (VSep) (-> nonogram :cols hints/col-hints)
               (HSep) (Label "┼") (HSep)

               (-> nonogram :rows hints/row-hints) (VSep) (playfield nonogram)]
    ))

(defn run [nonogram]
  (let [window (Window (nonogram :title) (nono-panel nonogram) :CENTERED)
        ]
    (doto (lgui/text-gui)
      (.addWindow window)
      (.waitForWindowToClose window))))
