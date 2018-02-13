(ns nono.gui
  (:require [lanterna.widgets :refer [Window VSep HSep Label GridPanel Button]]
            [lanterna.gui :as lgui]
            [nono.lanterna-gui :refer [ViewButton ViewLabel]]
            [nono.hints :as hints]
            [nono.nonogram :as ng]
            [nono.game :as game]
            [clojure.string :as string]
            [clojure.core.matrix :as mx]
            [schema.core :as s :refer [def defn]]))

(defn- statline [game]
  (ViewLabel
    (fn []
      (str
        (-> @game :nonogram :title) \newline
        (-> @game :nonogram :cols) \× (-> @game :nonogram :rows) \newline
        (-> @game :col) \, (-> @game :row)
        ))))

(def tiles {:full "██" :empty "╶ " :??? "░░"})
(def next-tile {:??? :full
                :full :empty
                :empty :full})
(defn- update-tile [game row col]
  (swap! game
         #(update-in % [:grid row col] next-tile)))
(defn- show-tile [game row col]
  (-> @game :grid (mx/mget row col) tiles))
(defn- CellButton [game [row col]]
  (ViewButton
    (partial show-tile game row col)
    (partial update-tile game row col)))

(defn- playfield
  [game]
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
    (swap! game #(assoc % :ui window))
    (doto (lgui/text-gui)
      (.addWindow window)
      (.waitForWindowToClose window))))
