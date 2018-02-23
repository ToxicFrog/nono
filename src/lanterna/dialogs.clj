(ns lanterna.dialogs
  "Constructors and utility functions for tinned Lanterna dialogs."
  (:import (java.lang Enum)
           (com.googlecode.lanterna.gui2.dialogs MessageDialog MessageDialogButton)))

(defn show-message-dialog
  [text-gui title text & buttons]
  (MessageDialog/showMessageDialog
    text-gui title text
    (into-array MessageDialogButton
                (map #(Enum/valueOf MessageDialogButton (name %)) buttons))))
