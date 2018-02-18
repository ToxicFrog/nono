(println "Loading program...")
; (set! *warn-on-reflection* true)

(ns nono.core
  (:gen-class)
  (:require [nono.nonogram :as ng]
            [nono.gui :as gui]
            [nono.game :as game]
            [clojure.spec.alpha :as spec]
            [expound.alpha :as expound]
            [schema.core :as s]))


(defn -main [& args]
  (println "Starting game...")
  (if (= (first args) "debug")
    (do
      (set! spec/*explain-out* expound/printer)
      (s/set-fn-validation! true)))
  ; (-> "puzzles/qr.json" ng/from-resource game/create-state gui/run)
  (-> 16903 ng/from-nonograms-org game/create-state gui/run))
