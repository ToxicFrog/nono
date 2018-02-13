(ns nono.core
  (:gen-class)
  (:require [clojure.string :as string]
            [nono.nonogram :as nonogram]
            [nono.gui :as gui]
            [nono.game :as game]
            [schema.core :as s]))

(s/set-fn-validation! true)

(defn -main [filename & args]
  (let [game (-> (nonogram/load filename) (game/create-state))]
    (gui/run game)))
