(ns nono.core
  (:gen-class)
  (:require [clojure.string :as string]
            [nono.nonogram :as nonogram]
            [nono.gui :as gui]
            [schema.core :as s]))

(s/set-fn-validation! true)

(defn -main [filename & args]
  (gui/run (nonogram/load filename))
  )
