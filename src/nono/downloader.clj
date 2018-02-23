(ns nono.downloader
  "A library for downloading nonograms from nonograms.org.
  Adapted from https://github.com/ThomasR/nonogram-solver/."
  (:require [http.async.client :as http]
            [clojure.core.matrix :as mx]
            [clojure.data.json :as json]))

(defn- get-url [url]
  (with-open [client (http/create-client)]
    (-> (http/GET client url)
        http/await
        http/string)))

(defn- extract-title [page]
  (some->> page
           (re-find #"«(.+?)»")
           second))

(defn- extract-data [page]
  (some->> page
           (re-find #"d *= *(\[.*?\]);")
           second
           json/read-str))

; Drawing instructions are of the form [col len colour row], meaning draw
; <len> cells of <colour> starting at <row> and <column>, moving rightwards.
; Note that the grid is implicitly initialized to 0 (empty).
; Black and white puzzles only use 0 and 1; multicoloured puzzles use values
; >0 for the colours. It's not yet known where the colourmap is stored.
(defn- draw [grid [col len colour row]]
  (mx/set-selection grid (dec row) (range (dec col) (+ col len -1)) colour))

(defn decode-grid [data]
  (let
    [decode (fn [[x y z q] a b c]
              (+ (* a (mod x q))
                 (* b (mod y q))
                 (* c (mod z q))))
     ; First four fields are unused, width, height, and offset (-5) to the
     ; start of the actual picture data.
     width (decode (data 1) 1 1 -1)
     height (decode (data 2) 1 1 -1)
     slice-at (+ 5 (decode (data 3) 1 1 -1))
     data (vec (drop slice-at data))
     ; First field of picture data is the (double-scrambled!) total number of
     ; drawing instructions.
     draw-count (data 0)
     draw-count (decode draw-count (mod (draw-count 0) (draw-count 3)) 2 1)
     ; Then the scrambling key for the actual drawing instructions
     key (data 1)
     ; And finally the drawing instructions themselves. Subtract the key from
     ; each one to get the actual instruction.
     draw-codes (->> data (drop 2) (take draw-count)
                     (map (fn [draw-code] (mx/sub draw-code key))))]
    (reduce
      draw
      (mx/matrix (mx/broadcast 0 [height width]))
      draw-codes)))

(defn get-puzzle [id]
  (let [page (get-url (str "http://www.nonograms.org/nonograms/i/" id))
        grid (some-> page extract-data decode-grid mx/matrix)]
    (if grid
      [(extract-title page) grid]
      nil)))
