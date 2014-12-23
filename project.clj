(defproject magnet "0.1.0-SNAPSHOT"
  :description "FIXME: write description"
  :url "http://example.com/FIXME"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.6.0"]
                 [org.clojure/clojurescript "0.0-2371"]
                 [org.clojure/core.async "0.1.346.0-17112a-alpha"]
                 [cljs-ajax "0.3.3"]
                 [com.andrewmcveigh/cljs-time "0.2.4"]
                 [reagent "0.4.3"]
                 [secretary "1.2.1"]
                 [figwheel "0.1.5-SNAPSHOT"]]
  :plugins [[com.cemerick/austin "0.1.5"]
            [codox "0.8.10"]
            [lein-cljsbuild "1.0.3"]
            [lein-figwheel "0.1.5-SNAPSHOT"]]
  :codox {:language :clojurescript
          :src-dir-uri "http://github.com/lnmnd/magnet.bez/blob/master/"
          :src-linenum-anchor-prefix "L"
          :project {:name "Magnet", :version "1.0.0", :description "Magnet loturak: bezeroa"}}
  :cljsbuild {
              :builds {
                       :dev
                       {:source-paths ["src"]
                        :compiler {
                                   :output-to "resources/public/magnet.js"
                                   :source-map "resources/public/magnet.js.map"
                                   :output-dir "resources/public/out"
                                   :optimizations :none}}
                       :prod
                       {:source-paths ["src"]
                        :compiler {
                                   :output-to "dist/js/magnet.js"
                                   :optimizations :advanced}}}}
  :figwheel {
             :http-server-root "public"
             :server-port 8080
             :css-dirs ["resources/public/css"]})
