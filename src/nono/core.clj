(ns nono.core
  (:gen-class)
  (:require [clojure.string :as string]
            [nono.nonogram :as nonogram]
            [nono.gui :as gui]
            [nono.game :as game]
            [clojure.spec.alpha :as spec]
            [expound.alpha :as expound]
            [schema.core :as s]))

(s/set-fn-validation! true)
(set! spec/*explain-out* expound/printer)

(defn -main [filename & args]
  (let [game (-> (nonogram/load filename) (game/create-state))]
    (gui/run game)))
