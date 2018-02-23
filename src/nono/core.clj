(println "Loading program...")
; (set! *warn-on-reflection* true)

(ns nono.core
  (:gen-class)
  (:require [clojure.spec.alpha :as spec]
            [expound.alpha :as expound]
            [nono.gui.mainmenu :refer [main-menu]]
            [lanterna.gui :as gui]
            [lanterna.settings :refer [PropertyTheme]]
            [clojure.java.io :as io]
            [schema.core :as s]))

(defn -main [& args]
  (println "Starting game...")
  (if (= (first args) "debug")
    (do
      (set! spec/*explain-out* expound/printer)
      (s/set-fn-validation! true)))
  (gui/create-and-run
    (-> "theme.properties" io/resource PropertyTheme)
    main-menu))
