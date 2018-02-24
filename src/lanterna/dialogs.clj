(ns lanterna.dialogs
  "Constructors and utility functions for tinned Lanterna dialogs."
  (:import (java.lang Enum)
           (com.googlecode.lanterna.gui2 Window$Hint)
           (com.googlecode.lanterna.gui2.dialogs
             MessageDialog MessageDialogButton WaitingDialog TextInputDialog ListSelectDialog)))

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

(defn show-number-dialog
  [text-gui title caption default]
  (TextInputDialog/showNumberDialog text-gui title caption default))

(defn show-selection-list
  [text-gui title caption items]
  (ListSelectDialog/showDialog text-gui title caption
                               ; FIXME -- we assume that there's always 5 rows of padding
                               ; in the dialog.
                               (- (.. text-gui getScreen getTerminalSize getRows) 5)
                               (into-array items)))
