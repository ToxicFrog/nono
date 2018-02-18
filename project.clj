(defproject nono "0.1.0-SNAPSHOT"
  :description "A TTY nonogram player"
  :url "https://github.com/toxicfrog/nono"
  :license {:name "Apache 2.0"
            :url "https://www.apache.org/licenses/LICENSE-2.0"}
  :dependencies [[org.clojure/clojure "1.9.0"]
                 [com.googlecode.lanterna/lanterna "3.0.0"]
                 [net.mikera/core.matrix "0.62.0"]
                 [expound "0.5.0"]
                 [org.clojure/data.json "0.2.6"]
                 [http.async.client "1.2.0"]
                 [prismatic/schema "1.1.7"]]
  ; :core.typed {:check [ttymor.core]}
  :main ^:skip-aot nono.core
  :target-path "target/%s"
  :profiles {:uberjar {:aot :all}})
