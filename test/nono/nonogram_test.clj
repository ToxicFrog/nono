(ns nono.nonogram-test
  (:require [clojure.test :refer :all]
            [nono.nonogram :refer :all]))

(def test-pattern
  "{
  \"title\": \"test pattern\",
  \"columns\": [[1,1],[2],[1,2]],
  \"rows\": [[1,1],[1],[3],[1]]
  }")

(def test-ngram
  {:title "test pattern"
   :width 3
   :height 4
   :grid [[:??? :??? :???]
          [:??? :??? :???]
          [:??? :??? :???]
          [:??? :??? :???]]
   ; :grid [[:full :empty :full]
   ;        [:empty :full :empty]
   ;        [:full :full :full]
   ;        [:empty :empty :full]]
   :hints {:col [[1 1] [2] [1 2]]
           :row [[1 1] [1] [3] [1]]}})

(deftest test-json->nonogram
  (testing "Loading nonogram from JSON"
    (is (= test-ngram
           (from-json test-pattern)))))
