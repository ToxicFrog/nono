(ns nono.gui.help
  "The help window."
  (:require [lanterna.dialogs :refer [show-message-dialog]]))

(def help-text
  "   space : fill box        h, ? : this screen
       x : clear box          c : check solution
       v : mark box '??'      q : quit game
       z : reset box
    -, _ : finish row with clear/full
    \\, | : finish column with clear/full")

(defn show-help [text-gui]
  (show-message-dialog text-gui "Help" help-text :OK))
