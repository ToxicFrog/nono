(ns nono.gui.help
  "The help window."
  (:import (com.googlecode.lanterna.gui2.dialogs MessageDialog MessageDialogButton)))

(def help-text
  "   space : fill box        h, ? : this screen
       x : clear box          c : check solution
       v : mark box '??'      q : quit game
       z : reset box
    -, _ : finish row with clear/full
    \\, | : finish column with clear/full")

(defn show-help [text-gui]
  (MessageDialog/showMessageDialog
    text-gui "Help" help-text
    (into-array MessageDialogButton [MessageDialogButton/OK])))
