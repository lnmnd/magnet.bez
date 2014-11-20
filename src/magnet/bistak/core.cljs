(ns magnet.bistak.core
  (:require [cljs.core.async :refer [put!]]
            [magnet.bistak.goiko-barra :as goiko-barra]
            [magnet.bistak.erdia :as erdia]            
            [magnet.bistak.oina :as oina]))

(defn main [{:keys [saio-kan liburu-kan iruzkin-kan saioa bidea azken-iruzkinak argitaletxeak aliburuak nliburuak niruzkinak liburua lib-irak]}]
  [:div {:class "row"}
   [:div {:class "medium-12 columns"}
    [goiko-barra/main {:saio-kan saio-kan
                       :saioa saioa}]
    [erdia/main {:saio-kan saio-kan
                 :saioa saioa
                 :liburu-kan liburu-kan
                 :iruzkin-kan iruzkin-kan
                 :bidea bidea
                 :azken-iruzkinak azken-iruzkinak
                 :argitaletxeak argitaletxeak
                 :aliburuak aliburuak
                 :nliburuak nliburuak
                 :niruzkinak niruzkinak
                 :liburua liburua
                 :lib-irak lib-irak}]
    [oina/main]]])
