(ns magnet.bistak.erdia.liburua
  (:require [cljs.core.async :refer [put!]]
            [reagent.core :as reagent :refer [atom]]
            [magnet.bistak.lagun :refer [formu-tratatu]]))

(defn- iruzkin-form [{:keys [iruzkin-kan libid]}]
  (let [edukia (atom "")]
    (fn [{:keys [iruzkin-kan libid]}]
      [:form {:id "iruzkin-form"}
       [:label "Edukia"]
       [:input {:type "text" :required true :on-change #(reset! edukia (-> % .-target .-value))}]
       [:input.button {:type "submit" :value "Bidali"
                       :on-click (fn [] (formu-tratatu "#iruzkin-form"
                                                       #(put! iruzkin-kan [:iruzkina-gehitu {:id libid
                                                                                             :edukia {:edukia @edukia}}])))}]])))


(defn- liburuaren-iruzkinak [irak]
  [:div
   [:h2 "Iruzkinak"]
   (if (empty? @irak)
     [:p "Iruzkinik ez."]
     (for [i @irak]
       ^{:key i}
       [:div.panel (:erabiltzailea i) " data" [:br] (:edukia i)]))])


(defn liburua [{:keys [saioa iruzkin-kan lib irak]}]
  [:div
   [:div.small-12.medium-6.columns
    [:h1 (:titulua @lib)]
    [:a {:href (:magnet @lib)} [:img {:src "img/magnet.gif" :alt "magnet"}] " " [:strong "Magnet lotura"]]
    [:div.row
     [:div.small-6.columns
      [:dl
       [:dt "Egileak"]
       [:dd (for [x (interpose ", " (:egileak @lib))]
              [:span x])]
       [:dt "Hizkuntza"]
       [:dd (:hizkuntza @lib)]
       [:dt "Generoa"]
       [:dd (:generoa @lib)]]]
     [:div.small-6.columns
      [:dl
       [:dt "Erabiltzailea"]
       [:dd (:erabiltzailea @lib)]
       [:dt "Urtea"]
       [:dd (:urtea @lib)]
       [:dt "Argitaletxea"]
       [:dd (:argitaletxea @lib)]]]]
    [:dl
     [:dt "Etiketak"]
     [:dd (for [x (interpose ", " (:etiketak @lib))]
            [:span x])]
     [:dt "Sinposia"]
     [:dd (:sinopsia @lib)]]]
   [:div.small-12.medium-6.columns
    [:img {:src (:azala @lib)}]]
   [:div.small-12.medium-12.columns
    [liburuaren-iruzkinak irak]
    (when (:hasita @saioa)
      [iruzkin-form {:iruzkin-kan iruzkin-kan
                     :libid (:id @lib)}])]])
