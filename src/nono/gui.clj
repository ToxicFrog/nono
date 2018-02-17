(ns nono.gui
  (:require [lanterna.widgets :refer [VSep HSep Label ViewLabel]]
            [lanterna.containers :refer [GridPanel]]
            [lanterna.settings :refer [PropertyTheme]]
            [lanterna.gui :refer [Window MultiWindowTextGUI]]
            [nono.lanterna-gui :refer [CellButton]]
            [nono.hints :as hints]
            [nono.game :as game]
            [clojure.core.matrix :as mx]
            [schema.core :as s :refer [def defn]]))

(def nono-theme (PropertyTheme "data/theme.properties"))

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
    (doto (MultiWindowTextGUI nono-theme)
      (.addWindow window)
      (.waitForWindowToClose window))))
