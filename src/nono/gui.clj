(ns nono.gui
  (:require [lanterna.widgets :refer [Window VSep HSep Label GridPanel Button]]
            [lanterna.gui :as lgui]
            [nono.lanterna-gui :refer [CellButton ViewLabel]]
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
        (-> @game :puzzle :width) \× (-> @game :puzzle :height) \newline
        (-> @game :col) \, (-> @game :row)))))

(defn- playfield
  [game]
  (GridPanel
    :width (-> @game :puzzle :width)
    :children (->> @game :puzzle :grid mx/index-seq
                   (map (partial CellButton game)))))

(defn- nono-panel [game]
  (GridPanel
    :width 3
    :children [(statline game) (VSep) (hints/col-hints game)
               (HSep) (Label "┼") (HSep)

               (hints/row-hints game) (VSep) (playfield game)]
    ))

(defn run :- s/Any [game :- game/GameAtom]
  (let [window (Window (-> @game :puzzle :title)
                       (nono-panel game)
                       :CENTERED)]
    (doto (lgui/text-gui)
      (.addWindow window)
      (.waitForWindowToClose window))))
