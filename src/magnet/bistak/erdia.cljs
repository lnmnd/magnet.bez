(ns magnet.bistak.erdia
  (:require [cljs.core.async :refer [put!]]
            [reagent.core :as reagent :refer [atom]]
            [magnet.bistak.lagun :refer [formu-tratatu]]
            [magnet.bistak.erdia.alboko-barra :refer [alboko-barra]]
            [magnet.bistak.erdia.liburua-gehitu :refer [liburua-gehitu]]))

; BISTAK
(defn erregistratu [kan erak]
  (let [erabiltzailea (atom "")
        pasahitza (atom "")
        izena (atom "")
        deskribapena (atom "")
        era-badago (atom false)]
    (fn [kan erak]
      [:div
       [:h2 "Erregistratu"]
       [:form {:id "erregistratu"}
        [:label "Erabiltzaile izena"
         [:input {:type "text" :required true
                  :on-change #(do (reset! erabiltzailea (-> % .-target .-value))
                                  (reset! era-badago (contains? (set @erak) @erabiltzailea))p)}]]
        (when @era-badago
          [:small.error (str @erabiltzailea " erabiltzailea existitzen da.")])
        [:label "Pasahitza"
         [:input {:type "password" :required true :on-change #(reset! pasahitza (-> % .-target .-value))}]]
        [:label "Izena"
         [:input {:type "text" :required true :on-change #(reset! izena (-> % .-target .-value))}]]
        [:label "Deskribapena"
         [:textarea {:rows "4" :on-change #(reset! deskribapena (-> % .-target .-value))}]]
        [:input.button {:type "submit" :value "Erregistratu"
                        :on-click (fn [x]
                                    (formu-tratatu "#erregistratu"
                                                   #(put! kan [:erregistratu {:erabiltzailea @erabiltzailea
                                                                              :pasahitza @pasahitza
                                                                              :izena @izena
                                                                              :deskribapena @deskribapena}])))}]]])))

(defn saioa-hasi [kan saioa]
  (let [era (atom "")
        pas (atom "")]
    (fn [kan saioa]
      [:div
       [:h2 "Saioa hasi"]
       [:form {:id "saioa-hasi" :method "POST"}
        [:label "Erabiltzaile izena"
         [:input {:type "text" :required true :on-change #(reset! era (-> % .-target .-value))}]]
        [:label "Pasahitza"
         [:input {:type "password" :required true :on-change #(reset! pas (-> % .-target .-value))}]]
        (when (:hasiera-okerra @saioa)
          [:small.error "Erabiltzaile edo pasahitz okerra."])
        [:input.button {:type "submit" :value "Saioa hasi"
                        :on-click #(formu-tratatu "#saioa-hasi"
                                                  (fn [] (put! kan [:saioa-hasi {:era @era :pas @pas}])))}]]])))

(defn profila [{:keys [saio-kan saioa]}]
  (let [pas (atom "")
        izen (atom "")
        des (atom "")
        ezab (atom "")]
    (fn [{:keys [saio-kan saioa]}]
      [:div
       [:h2 "Nire profila"]
       [:div.small-12.medium-6.columns
        [:h3 "Oraingo datuak"]
        [:p "Erabiltzailea: " (:erabiltzailea @saioa)]
        [:p "Izena: " (:izena @saioa)]
        [:p "Deskribapena: " (:deskribapena @saioa)]]
       [:div.small-12.medium-6.columns
        [:h3 "Datu berriak"]
        [:form {:id "profila"}
         [:label "Pasahitza"
          [:input {:type "password" :required true :on-change #(reset! pas (-> % .-target .-value))}]]
         [:label "Izena"
          [:input {:type "text" :required true :on-change #(reset! izen (-> % .-target .-value))}]]
         [:label "Deskribapena"
          [:textarea {:rows "4" :on-change #(reset! des (-> % .-target .-value))}]]
         [:input.button {:type "submit" :value "Aldatu"
                         :on-click (fn [] (formu-tratatu "#profila"
                                                         #(do (put! saio-kan [:erabiltzailea-aldatu {:era (:erabiltzailea @saioa)
                                                                                                     :pas @pas
                                                                                                     :izen @izen
                                                                                                     :des @des}])
                                                              false)))}]]
        [:h3 "Ezabatu"]
        [:p "Kontua ezabatu nahi baduzu idatzi \"Bai, ezabatu nahi dut.\" ondorengo eremuan:"]
        [:input {:type "text" :on-change #(reset! ezab (-> % .-target .-value))}]
        [:a.button {:href "#" :on-click #(if (= @ezab "Bai, ezabatu nahi dut.")
                                           (put! saio-kan [:erabiltzailea-ezabatu])
                                           false)}
         "Ezabatu"]]])))

(defn azken-liburuak [libuk]
  [:div
   (for [li @libuk]
     ^{:key li} [:a {:href (str "#/liburuak/" (:id li))}
                 [:div.small-6.medium-4.columns
                  [:img {:src (:azala li)}]
                  [:div.panel
                   [:h5 (:titulua li)]
                   [:h6.subheader (interpose ", " (:egileak li))]
                   [:div.row
                    [:div.small-8.columns
                     [:p.left (:erabiltzailea li)]]
                    [:div.small-4.columns
                     (:iruzkin_kopurua li) " ✍"
                     [:br]
                     (:gogoko_kopurua li) " ♥"]]]]])])

; TODO saioa hasita?
(defn iruzkin-form [{:keys [iruzkin-kan libid]}]
  (let [edukia (atom "")]
    (fn [{:keys [iruzkin-kan libid]}]
      [:form {:id "iruzkin-form"}
       [:label "Edukia"]
       [:input {:type "text" :required true :on-change #(reset! edukia (-> % .-target .-value))}]
       [:input.button {:type "submit" :value "Bidali"
                       :on-click (fn [] (formu-tratatu "#iruzkin-form"
                                                       #(put! iruzkin-kan [:iruzkina-gehitu {:id libid
                                                                                             :edukia {:edukia @edukia}}])))}]])))

(defn liburuaren-iruzkinak [irak]
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

(defn nire-liburuak [kan liburuak]
  [:div
   [:h1 "Nire liburuak"]
   (if (empty? @liburuak)
     [:p "Libururik ez."]
     [:ul (for [l @liburuak]
            ^{:key l} [:li [:a {:href "#" :on-click #(do (when (js/confirm "Seguru liburua ezabatu nahi duzula?")
                                                           (put! kan [:liburua-ezabatu (:id l)]))
                                                         false)} "X"]
                       " " [:a {:href (str "#/liburuak/" (:id l))} (:titulua l)]])])])

(defn nire-iruzkinak [kan iruzkinak]
  [:div
   [:h1 "Nire iruzkinak"]
   (if (empty? @iruzkinak)
     [:p "Iruzkinik ez."]
     [:ul (for [i @iruzkinak]
            ^{:key i} [:li [:a {:href "#" :on-click #(do (when (js/confirm "Seguru iruzkina ezabatu nahi duzula?")
                                                           (put! kan [:iruzkina-ezabatu (:id i)]))
                                                         false)} "X"]
                       " " [:a {:href (str "#/liburuak/" (:id i))}
                            (let [n 32
                                  edu (:edukia i)]
                              (if (< (count edu) n)
                                edu
                                (concat (take n edu) "...")))]])])])

(defn bilatu []
  [:div
   [:h1 "Bilatu"]
   [:p "todo"]])

(defn nagusia [{:keys [saio-kan saioa liburu-kan iruzkin-kan bidea erabiltzaileak egileak argitaletxeak generoak etiketak aliburuak nliburuak niruzkinak lib lib-irak]}]
  (let [[bid bal] @bidea]
    [:div.medium-8.columns
     [:div.row
      (case bid
        :index [azken-liburuak aliburuak]
        :erregistratu [erregistratu saio-kan erabiltzaileak]
        :saioa-hasi [saioa-hasi saio-kan saioa]
        :profila [profila {:saio-kan saio-kan
                           :saioa saioa}]
        :liburua-gehitu [liburua-gehitu liburu-kan egileak argitaletxeak generoak etiketak]
        :nire-liburuak [nire-liburuak liburu-kan nliburuak]
        :nire-iruzkinak [nire-iruzkinak iruzkin-kan niruzkinak]
        :liburua [liburua {:saioa saioa
                           :iruzkin-kan iruzkin-kan
                           :lib lib
                           :irak lib-irak}]
        :bilatu [bilatu]
        nil)]]))

(defn main [{:keys [saio-kan saioa liburu-kan iruzkin-kan bidea azken-iruzkinak erabiltzaileak egileak argitaletxeak generoak etiketak aliburuak nliburuak niruzkinak liburua lib-irak]}]
  [:div.row
   [alboko-barra azken-iruzkinak]
   [nagusia {:saio-kan saio-kan
             :saioa saioa
             :liburu-kan liburu-kan
             :iruzkin-kan iruzkin-kan
             :bidea bidea
             :erabiltzaileak erabiltzaileak
             :egileak egileak
             :argitaletxeak argitaletxeak
             :generoak generoak
             :etiketak etiketak
             :aliburuak aliburuak
             :nliburuak nliburuak
             :niruzkinak niruzkinak
             :lib liburua
             :lib-irak lib-irak}]])
