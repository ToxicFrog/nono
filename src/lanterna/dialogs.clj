(ns lanterna.dialogs
  "Constructors and utility functions for tinned Lanterna dialogs."
  (:import (java.lang Enum)
           (com.googlecode.lanterna.gui2 Window$Hint)
           (com.googlecode.lanterna.gui2.dialogs MessageDialog MessageDialogButton
                                                 WaitingDialog)))

(defn show-message-dialog
  [text-gui title text & buttons]
  (MessageDialog/showMessageDialog
    text-gui title text
    (into-array MessageDialogButton
                (map #(Enum/valueOf MessageDialogButton (name %)) buttons))))

(defn show-waiting-dialog
  [text-gui title text]
  (let [waiting (WaitingDialog/showDialog text-gui title text)]
    (.setHints waiting [Window$Hint/MODAL Window$Hint/CENTERED])
    (.updateScreen text-gui)
    waiting))
