(ns nono.core
  (:gen-class)
  (:require [clojure.string :as string]
            [nono.nonogram :as nonogram]
            [nono.gui :as gui])
  )

(defn -main [filename & args]
  (gui/run (nonogram/load filename))
  )
