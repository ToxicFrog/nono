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


(defn -main [& args]
  (println "Starting game...")
  (if (= (first args) "debug")
    (set! spec/*explain-out* expound/printer)
    (s/set-fn-validation! true))
  (let [game (-> "puzzles/qr.json"
                 io/resource
                 slurp
                 ng/json->nonogram
                 game/create-state)]
    (gui/run game)))
