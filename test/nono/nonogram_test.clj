(ns nono.nonogram-test
  (:require [clojure.test :refer :all]
            [nono.nonogram :refer :all]))

(def test-pattern
  (str
    "test pattern\n"
    "#.#\n"
    ".#.\n"
    "###\n"
    "..#\n"))

(def test-ngram
  {:title "test pattern"
   :width 3
   :height 4
   :grid [[:full :empty :full]
          [:empty :full :empty]
          [:full :full :full]
          [:empty :empty :full]]
   :hints {:col [[1 1] [2] [1 2]]
           :row [[1 1] [1] [3] [1]]}})

(deftest test-str->nonogram
  (testing "Loading nonogram from string"
    (is (= test-ngram
           (str->nonogram test-pattern)))))
