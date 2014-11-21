(ns magnet.bistak.erdia
  (:require [cljs.core.async :refer [put!]]
            [reagent.core :as reagent :refer [atom]]))

(defn formu-ez-bidali [formu]
  (.addEventListener formu "submit"
                     (fn [ger]
                       (.preventDefault ger)
                       false)))

(defn baliozko-formu? [formu]
  (or (not (.-checkValidity formu))
      (.checkValidity formu)))

(defn formu-tratatu [sel f]
  (let [formu (.querySelector js/document sel)]
    (formu-ez-bidali formu)
    (when (baliozko-formu? formu)
      (f))))

; BISTAK
(defn azken-iruzkina [ir]
  [:a {:href (str "#/liburuak/" (:liburua ir))}
   [:div.panel.radius
    [:h5 "Liburu titulua"]
    [:blockquote (:edukia ir) [:cite (:erabiltzailea ir) " - " (:data ir)]]]])

(defn azken-iruzkinak [iruzkinak]
  [:div
   [:h3 "Azken iruzkinak"
    (for [ir @iruzkinak]
      ^{:key ir} [azken-iruzkina ir])]])

(defn alboko-barra [iruzkinak]
  [:div.medium-4.small-12.hide-for-small.columns
   [:img {:src "img/liburua.jpg"}]
   [:div.panel
    [:h3 "Titulua"]
    [:h5 {:class "subheader"} "Liburu garrantzitsu baten datuak."]]
   [azken-iruzkinak iruzkinak]])

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
                   [:p.left (:gogoko_kopurua li) " gogoko"]
                   [:p.right (:iruzkin_kopurua li) " iruzkin"]]]])])

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
   (for [i @irak]
     ^{:key i}
     [:div.panel (:erabiltzailea i) " data" [:br] (:edukia i)])])

(defn liburua [{:keys [saioa iruzkin-kan lib irak]}]
  [:div
   [:div.small-12.medium-6.columns
    [:h1 (:titulua @lib)]
    [:a {:href (:magnet @lib)} (:magnet @lib)]
    [:p "Egileak: "(for [e (:egileak @lib)]
                     [:span e " "])]
    [:p "Urtea" (:urtea @lib)]
    [:p (:sinopsia @lib)]]
   [:div.small-12.medium-6.columns
    [:img {:src (:azala @lib)}]]
   [:div.small-12.medium-12.columns
    [liburuaren-iruzkinak irak]
    (when (:hasita @saioa)
      [iruzkin-form {:iruzkin-kan iruzkin-kan
                     :libid (:id @lib)}])]])

(defn liburua-gehitu [kan egile-guztiak argitaletxeak generoak]
  (let [bidali-klikatuta (atom false)
        epub (atom "")
        epub-edukia-aldatu (fn [f]
                             (let [fr (js/FileReader.)]
                               (set! (.-onload fr)
                                     (fn [ger]
                                       (reset! epub (subs (.-result (.-target ger)) (count "data:application/epub+zip;base64,")))))
                               (.readAsDataURL fr f)))
        epub-lortu (fn [tar]
                     (let [fitx (.item (.-files tar) 0)]
                       (epub-edukia-aldatu fitx)))
        titulua (atom "")
        egileak (atom (sorted-map))
        egile-kop (atom 0)
        egilea-gehitu (fn [egilea]
                        (let [id (swap! egile-kop inc)]
                          (swap! egileak assoc id {:id id :egilea egilea})))
        egilea-ezabatu #(swap! egileak dissoc %)
        egile-zerrenda (fn [egak]
                         (map #(:egilea (second %)) egak))
        egilerik-gehituta #(> (count (egile-zerrenda @egileak)) 0)
        hizkuntza (atom "")
        sinopsia (atom "")
        argitaletxea (atom "")
        urtea (atom "")
        generoa (atom "")
        etiketak (atom (sorted-map))
        etiketa-kop (atom 0)
        etiketa-gehitu (fn [etiketa]
                        (let [id (swap! etiketa-kop inc)]
                          (swap! etiketak assoc id {:id id :etiketa etiketa})))
        etiketa-ezabatu #(swap! etiketak dissoc %)
        etiketa-zerrenda (fn [etik]
                           (map #(:etiketa (second %)) etik))        
        azala (atom "")
        azala-img-aldatu (fn [f]
                           (let [fr (js/FileReader.)]
                             (set! (.-onload fr)
                                   (fn [ger]
                                     (reset! azala (subs (.-result (.-target ger)) (count "data:image/jpeg;base64,")))
                                     (set! (.-src (js/document.getElementById "liburua-gehitu-azala-img"))
                                           (.-result (.-target ger)))))
                             (.readAsDataURL fr f)))
        azala-lortu (fn [tar]
                      (let [fitx (.item (.-files tar) 0)]
                        (azala-img-aldatu fitx)))]
    (fn [kan egile-guztiak argitaletxeak generoak]
      [:div
       [:h1 "Liburua gehitu"]
       [:form {:id "liburua-gehitu"}
        [:label "Epub"]
        [:input {:type "file" :required true :on-change #(epub-lortu (-> % .-target))}]
        [:label "Titulua"]
        [:input {:type "text" :required true :max-length "256" :on-change #(reset! titulua (-> % .-target .-value))}]
        [:label "Egileak"]
        [:div.row.collapse
         [:div.small-10.columns
          [:input {:type "text" :id "egilea" :list "liburua-gehitu-egileak"}]
          [:datalist {:id "liburua-gehitu-egileak"}
           (for [x @egile-guztiak]
             [:option {:value x}])]]
         [:div.small-2.columns
          [:a.button.postfix {:on-click #(do (egilea-gehitu (.-value (.querySelector js/document "#egilea")))
                                             (set! (.-value (.querySelector js/document "#egilea")) ""))}
           "Gehitu"]]]
        (when (and @bidali-klikatuta (not (egilerik-gehituta)))
          [:small.error "Egile bat gutxienez gehitu."])
        [:ul
         (for [e @egileak]
           [:li [:a {:on-click #(egilea-ezabatu (:id (second e)))} "X"] " " (:egilea (second e))])]
        [:label "Hizkuntza"]
        [:input {:type "text" :required true :max-length "256" :list "liburua-gehitu-hizkuntzak" :on-change #(reset! hizkuntza (-> % .-target .-value))}]
        [:datalist {:id "liburua-gehitu-hizkuntzak"}
         [:option {:value "Euskara"}]
         [:option {:value "Gaztelania"}]
         [:option {:value "Frantsesa"}]
         [:option {:value "Ingelesa"}]]
        [:label "Sinopsia"]
        [:textarea {:type "text" :required true :rows "4" :max-length "256" :on-change #(reset! sinopsia (-> % .-target .-value))}]        
        [:label "Argitaletxea"]
        [:input {:type "text" :max-length "256" :list "liburua-gehitu-argitaletxeak" :on-change #(reset! argitaletxea (-> % .-target .-value))}]
        [:datalist {:id "liburua-gehitu-argitaletxeak"}
         (for [a @argitaletxeak]
           [:option {:value a}])]
        [:label "Urtea"]
        [:input {:type "number" :required true :max-length "4" :on-change #(reset! urtea (-> % .-target .-value))}]
        [:label "Generoa"]
        [:input {:type "text" :max-length "256" :list "liburua-gehitu-generoak" :on-change #(reset! generoa (-> % .-target .-value))}]
        [:datalist {:id "liburua-gehitu-generoak"}
         (for [x @generoak]
           [:option {:value x}])]
        [:label "Etiketak"]
        [:div.row.collapse
         [:div.small-10.columns
          [:input {:type "text" :id "etiketa"}]]
         [:div.small-2.columns
          [:a.button.postfix {:on-click #(do (etiketa-gehitu (.-value (.querySelector js/document "#etiketa")))
                                             (set! (.-value (.querySelector js/document "#etiketa")) ""))}
           "Gehitu"]]]
        [:ul
         (for [e @etiketak]
           [:li [:a {:on-click #(etiketa-ezabatu (:id (second e)))} "X"] " " (:etiketa (second e))])]        
        [:label "Azala"]
        [:img {:src "img/liburua.jpg" :id "liburua-gehitu-azala-img" :width "256" :height "256"}]
        [:input {:type "file" :required true :id "liburua-gehitu-azala" :on-change #(azala-lortu (-> % .-target))}]
        [:input.button {:type "submit" :value "Gehitu"
                        :on-click (fn []
                                    (reset! bidali-klikatuta true)
                                    (when (egilerik-gehituta)
                                      (formu-tratatu "#liburua-gehitu"
                                                     #(put! kan [:liburua-gehitu {:epub @epub
                                                                                  :titulua @titulua
                                                                                  :egileak (egile-zerrenda @egileak)
                                                                                  :hizkuntza @hizkuntza
                                                                                  :sinopsia @sinopsia
                                                                                  :argitaletxea @argitaletxea
                                                                                  :urtea @urtea
                                                                                  :generoa @generoa
                                                                                  :etiketak (etiketa-zerrenda @etiketak)
                                                                                  :azala @azala}]))))}]]])))

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

(defn nagusia [{:keys [saio-kan saioa liburu-kan iruzkin-kan bidea erabiltzaileak egileak argitaletxeak generoak aliburuak nliburuak niruzkinak lib lib-irak]}]
  (let [[bid bal] @bidea]
    [:div.medium-8.columns
     [:div.row
      (case bid
        :index [azken-liburuak aliburuak]
        :erregistratu [erregistratu saio-kan erabiltzaileak]
        :saioa-hasi [saioa-hasi saio-kan saioa]
        :profila [profila {:saio-kan saio-kan
                           :saioa saioa}]
        :liburua-gehitu [liburua-gehitu liburu-kan egileak argitaletxeak generoak]
        :nire-liburuak [nire-liburuak liburu-kan nliburuak]
        :nire-iruzkinak [nire-iruzkinak iruzkin-kan niruzkinak]
        :liburua [liburua {:saioa saioa
                           :iruzkin-kan iruzkin-kan
                           :lib lib
                           :irak lib-irak}]
        :bilatu [bilatu]
        nil)]]))

(defn main [{:keys [saio-kan saioa liburu-kan iruzkin-kan bidea azken-iruzkinak erabiltzaileak egileak argitaletxeak generoak aliburuak nliburuak niruzkinak liburua lib-irak]}]
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
             :aliburuak aliburuak
             :nliburuak nliburuak
             :niruzkinak niruzkinak
             :lib liburua
             :lib-irak lib-irak}]])
