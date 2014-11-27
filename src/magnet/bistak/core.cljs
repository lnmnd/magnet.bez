(ns magnet.bistak.core
  (:require [cljs.core.async :refer [put!]]
            [reagent.core :as reagent :refer [atom]]
            [magnet.bistak.goiko-barra :as goiko-barra]
            [magnet.bistak.erdia :as erdia]            
            [magnet.bistak.oina :as oina]))

(defn main [{:keys [saio-kan liburu-kan iruzkin-kan saioa bidea azken-iruzkinak erabiltzaileak egileak argitaletxeak generoak etiketak aliburuak nliburuak niruzkinak ngogokoak liburua lib-irak]}]
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
                 :erabiltzaileak erabiltzaileak
                 :egileak egileak
                 :argitaletxeak argitaletxeak
                 :generoak generoak
                 :etiketak etiketak
                 :aliburuak aliburuak
                 :nliburuak nliburuak
                 :niruzkinak niruzkinak
                 :ngogokoak ngogokoak
                 :liburua liburua
                 :lib-irak lib-irak}]
    [oina/main]]])
