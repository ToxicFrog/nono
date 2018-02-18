(ns nono.gui
  (:require [lanterna.widgets :refer [VSep HSep Label]]
            [lanterna.containers :refer [GridPanel]]
            [lanterna.settings :refer [PropertyTheme]]
            [lanterna.gui :refer [Window MultiWindowTextGUI]]
            [nono.gui.puzzle-view :refer [puzzle-view]]
            [nono.gui.hints :refer [hint-panel]]
            [nono.game :as game]
            [clojure.core.matrix :as mx]
            [clojure.java.io :as io]
            [schema.core :as s :refer [def defn]]))

(defn- statline [game]
  (Label "Nono v0.1\n\nPress h\nfor help"))

(defn- nono-panel [game]
  (GridPanel
    :width 3
    :children [      (statline game)  (VSep)   (hint-panel game :col)
                          (HSep)   (Label "┼") (HSep)
               (hint-panel game :row) (VSep)   (puzzle-view game)]
    ))

(defn run :- s/Any [game :- game/GameAtom]
  (let [window (Window (-> @game :puzzle :title)
                       (nono-panel game)
                       :CENTERED)
        text-gui (MultiWindowTextGUI (-> "theme.properties" io/resource PropertyTheme))]
    (doto text-gui
      (.addWindow window)
      (.waitForWindowToClose window))
    (.close (.getScreen text-gui))))
