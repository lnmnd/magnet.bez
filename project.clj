(defproject magnet "0.1.0-SNAPSHOT"
  :description "FIXME: write description"
  :url "http://example.com/FIXME"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.6.0"]
                 [org.clojure/clojurescript "0.0-2371"]
                 [org.clojure/core.async "0.1.346.0-17112a-alpha"]
                 [cljs-ajax "0.3.3"]
                 [reagent "0.4.3"]
                 [secretary "1.2.1"]]
  :plugins [[cider/cider-nrepl "0.7.0"]
            [com.cemerick/austin "0.1.5"]
            [codox "0.8.10"]
            [lein-cljsbuild "1.0.3"]]
  :codox {:language :clojurescript
          :include ["magnet.core"]
          :src-dir-uri "http://github.com/lnmnd/magnet.bez/blob/master/"
          :src-linenum-anchor-prefix "L"}
  :cljsbuild {
              :builds [{
                        :source-paths ["src"]
                        :compiler {
                                   :output-to "magnet.js"
                                   :source-map "magnet.js.map"
                                   :output-dir "out"
                                   :optimizations :none}}]})
