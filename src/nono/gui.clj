(ns nono.gui
  (:require [lanterna.widgets :refer [VSep HSep Label ViewLabel]]
            [lanterna.containers :refer [GridPanel]]
            [lanterna.settings :refer [PropertyTheme]]
            [lanterna.gui :refer [Window MultiWindowTextGUI]]
            [nono.gui.puzzle-view :refer [puzzle-view]]
            [nono.hints :as hints]
            [nono.game :as game]
            [clojure.core.matrix :as mx]
            [schema.core :as s :refer [def defn]]))

(defn- statline [game]
  (ViewLabel
    (fn []
      (str
        (-> @game :puzzle :width) \× (-> @game :puzzle :height) \newline
        (-> @game :col) \, (-> @game :row)))))

(defn- nono-panel [game]
  (GridPanel
    :width 3
    :children [(statline game)        (VSep)   (hints/col-hints game)
               (HSep)              (Label "┼") (HSep)
               (hints/row-hints game) (VSep)   (puzzle-view game)]
    ))

(defn run :- s/Any [game :- game/GameAtom]
  (let [window (Window (-> @game :puzzle :title)
                       (nono-panel game)
                       :CENTERED)]
    (doto (MultiWindowTextGUI (PropertyTheme "data/theme.properties"))
      (.addWindow window)
      (.waitForWindowToClose window))))
