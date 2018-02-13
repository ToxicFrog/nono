(ns nono.gui
  (:require [lanterna.widgets :refer [Window VSep HSep Label GridPanel Button]]
            [lanterna.gui :as lgui]
            [nono.hints :as hints]
            [nono.nonogram :as ng]
            [nono.game :as game]
            [clojure.string :as string]
            [clojure.core.matrix :as mx]
            [schema.core :as s :refer [def defn]]))

(defn- statline [game]
  (let [{:keys [width height]} (@game :nonogram)]
    (Label (str width \× height))))
; SmartLabel (...status function...)

(def tiles {:full "██" :empty "╶ " :??? "░░"})
(def next-tile {:??? :full
                :full :empty
                :empty :full})
(defn- update-tile [row col game]
  (update-in game [:grid row col] next-tile))
(defn- CellButton [game [row col]]
  (let [state (-> @game :grid (mx/mget row col))]
    (Button (tiles state)
            (fn toggle-tile []
              ;(println "Button at " col "," row " pressed!")
              (swap! game (partial update-tile row col))
              ))))

(defn- playfield
  [game]
  (println
    (->> @game :grid mx/index-seq))
  (GridPanel
    :width (-> @game :nonogram :width)
    :children (->> @game :grid mx/index-seq
                   (map (partial CellButton game)))))

(defn- nono-panel [game]
  (GridPanel
    :width 3
    :children [(statline game) (VSep) (-> @game :nonogram :col-hints hints/col-hints)
               (HSep) (Label "┼") (HSep)

               (-> @game :nonogram :row-hints hints/row-hints) (VSep) (playfield game)]
    ))

(defn run :- s/Any [game :- game/Game]
  (let [window (Window (-> @game :nonogram :title)
                       (nono-panel game)
                       :CENTERED)]
    (doto (lgui/text-gui)
      (.addWindow window)
      (.waitForWindowToClose window))))
