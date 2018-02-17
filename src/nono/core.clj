(println "Loading program...")
; (set! *warn-on-reflection* true)

(ns nono.core
  (:gen-class)
  (:require [clojure.string :as string]
            [nono.nonogram :as ng]
            [nono.gui :as gui]
            [nono.game :as game]
            [clojure.spec.alpha :as spec]
            [expound.alpha :as expound]
            [schema.core :as s]))

(println "Starting game...")

(s/set-fn-validation! true)
(set! spec/*explain-out* expound/printer)

(defn -main [filename & args]
  (let [game (-> (ng/file->nonogram filename) (game/create-state))]
    (gui/run game)))
