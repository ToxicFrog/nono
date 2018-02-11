(ns nono.gui
  (:require [lanterna.widgets :refer [Window VSep HSep Label GridPanel Button]]
            [lanterna.gui :as lgui]
            [nono.hints :as hints]
            [nono.nonogram :as ng]
            [clojure.string :as string])
  )

(defn- statline [nonogram]
  (Label (str
          ; (nonogram :title) \newline
          (count (nonogram :cols)) \× (count (nonogram :rows)) \newline
          )))

(def doubler {\# "██" \. "╶╴"})
(defn- CellButton [[[x y] cell]]
  (Button (doubler cell)
          (fn [] (println "Button at " x "," y " pressed!"))))

(defn- playfield
  [nonogram]
  (GridPanel
    :width (nonogram :width)
    :children (map CellButton (ng/cells nonogram))))

(defn- nono-panel [nonogram]
  (GridPanel
    :width 3
    :children [(statline nonogram) (VSep) (-> nonogram :col-hints hints/col-hints)
               (HSep) (Label "┼") (HSep)

               (-> nonogram :row-hints hints/row-hints) (VSep) (playfield nonogram)]
    ))

(defn run [nonogram]
  (let [window (Window (nonogram :title) (nono-panel nonogram) :CENTERED)
        ]
    (doto (lgui/text-gui)
      (.addWindow window)
      (.waitForWindowToClose window))))
