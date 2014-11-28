(ns magnet.bistak.erdia.liburua
  (:require [cljs.core.async :refer [put!]]
            [reagent.core :as reagent :refer [atom]]
            [magnet.bistak.lagun :refer [formu-tratatu]]))

(defn- iruzkin-form [{:keys [iruzkin-kan libid]}]
  (let [gurasoak (atom (sorted-map))
        guraso-kop (atom 0)
        gurasoa-gehitu (fn [gu]
                         (let [id (swap! guraso-kop inc)]
                           (swap! gurasoak assoc id {:id id :gurasoa gu})))
        gurasoa-ezabatu #(swap! gurasoak dissoc %)
        guraso-zerrenda (fn []
                          (map #(:gurasoa (second %)) @gurasoak))
        edukia (atom "")]
    (fn [{:keys [iruzkin-kan libid]}]
      [:div.panel
       [:form {:id "iruzkin-form"}
        [:div.row.collapse
         [:label "Gurasoak"]
         [:div.small-10.columns
          [:input {:type "text" :id "gurasoa" :placeholder "1,2"}]]
         [:div.small-2.columns
          [:a.button.postfix {:on-click #(do (let [xs (clojure.string.split (.-value (.querySelector js/document "#gurasoa")) #",")]
                                               (doseq [x xs]
                                                 (gurasoa-gehitu x)))
                                             (set! (.-value (.querySelector js/document "#gurasoa")) ""))}
           "Gehitu"]]]
        [:ul
         (for [x @gurasoak]
           [:li [:a {:on-click #(gurasoa-ezabatu (:id (second x)))} "X"] " " (:gurasoa (second x))])]        
        [:label "Edukia"]
        [:textarea {:rows "4" :required true :max-length "256" :value @edukia :on-change #(reset! edukia (-> % .-target .-value))}]
        [:input.button {:type "submit" :value "Bidali"
                        :on-click (fn [] (formu-tratatu "#iruzkin-form"
                                                        #(do (put! iruzkin-kan [:iruzkina-gehitu {:id libid
                                                                                                  :edukia {:gurasoak (guraso-zerrenda)
                                                                                                           :edukia @edukia}}])
                                                             (reset! gurasoak (sorted-map))
                                                             (reset! edukia ""))))}]]])))


(defn- liburuaren-iruzkinak [irak]
  [:div
   [:h2 "Iruzkinak"]
   (if (empty? @irak)
     [:p "Iruzkinik ez."]
     (for [i @irak]
       ^{:key i}
       [:div.panel {:id (:id i)}
        [:ul.inline-list
         (for [x (:gurasoak i)]
           [:li [:a {:href (str "#" x)} ">>" x]])]
        (when (:id i)
          [:a {:href (str "#" (:id i))} "#" (:id i)]) " "
        (:erabiltzailea i) " data" [:br] (:edukia i)
        [:ul.inline-list
         (for [x (:erantzunak i)]
           [:li [:a {:href (str "#" x)} ">>" x]])]]))])


(defn liburua [{:keys [saioa liburu-kan iruzkin-kan lib irak ngogokoak]}]
  (let [gogokoetan-dut? (some #(= (:id %) (:id @lib)) @ngogokoak)]
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
         [:dt "Igotze data"]
         [:dd (take 10 (:igotze_data @lib))] ; eguna hartu
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
      [:img {:src (:azala @lib)}]
      (when (:hasita @saioa)
        (if gogokoetan-dut?
          [:p "Liburua gogoko dut"]
          [:p [:a {:href "#" :on-click #(do (put! liburu-kan [:gogokoetan-sartu (:id @lib)])
                                            false)} "Liburu hau gogoko dut!"]]))]
     [:div.small-12.medium-12.columns
      [liburuaren-iruzkinak irak]
      (when (:hasita @saioa)
        [iruzkin-form {:iruzkin-kan iruzkin-kan
                       :libid (:id @lib)}])]]))
