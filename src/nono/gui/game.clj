(ns nono.gui.game
  (:require [lanterna.widgets :refer [VSep HSep Label ViewLabel]]
            [lanterna.containers :refer [GridPanel]]
            [lanterna.settings :refer [PropertyTheme Theme]]
            [lanterna.gui :refer [Window run-window]]
            [nono.gui.puzzle-view :refer [puzzle-view]]
            [nono.gui.hints :refer [hint-panel]]
            [nono.game :as game]
            [clojure.java.io :as io]
            [schema.core :as s :refer [def defn]]))

(defn statline-labeler [this game]
  ; TODO: move themes into a common file, or make a smarter PropertyTheme or something.
  (let [victory-theme (Theme "#00FF80" "#000000" :bold)
        defeat-theme (Theme "#000000" "#FF0000" :bold)])
  (cond
    ; (game/won? game)  (do (.setTheme this (Theme "#00000" "#FF0000" :bold))
    ;                    "❈❈❈❈❈❈❈\n❈ YOU ❈\n❈ WIN ❈\n❈❈❈❈❈❈❈")
    ; (game/lost? game) (do (.setTheme this (Theme "#00000" "#FF0000" :bold))
    ;                    "🕱🕱🕱🕱🕱🕱\n🕱GAME🕱\n🕱OVER🕱\n🕱🕱🕱🕱🕱🕱")
    (game/won? game)  (.setTheme this )
    (game/lost? game) (.setTheme this (Theme "#000000" "#FF0000" :bold)))
  (str
    (-> @game :puzzle :width) \× (-> @game :puzzle :height) \newline
    \( (-> @game :col) \, (-> @game :row) \) \newline
    "ERR:" (-> @game :errors) \newline
    "h:help"))

(defn- statline [game]
  (ViewLabel statline-labeler game))

(defn- nono-panel [game]
  (GridPanel
    :width 3
    :children [      (statline game)  (VSep)   (hint-panel game :col)
                          (HSep)   (Label "┼") (HSep)
               (hint-panel game :row) (VSep)   (puzzle-view game)]
    ))

(defn play-game :- s/Any
  [text-gui :- s/Any, game :- game/GameAtom]
  (run-window
    text-gui
    (Window (-> @game :puzzle :title)
            (nono-panel game)
            :centered)))
