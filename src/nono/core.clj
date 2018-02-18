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
            [clojure.java.io :as io]
            [schema.core :as s]))

(println "Starting game...")

(set! spec/*explain-out* expound/printer)

(defn -main [& args]
  (if (= (first args) "debug")
    (s/set-fn-validation! true))
  (let [game (-> "puzzles/qr.json"
                 io/resource
                 slurp
                 ng/json->nonogram
                 game/create-state)]
    (gui/run game)))
