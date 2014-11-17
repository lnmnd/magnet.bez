(ns magnet.bistak
  (:require [cljs.core.async :refer [put!]]))

(defn goiko-barra [{:keys [saio-kan saioa]}]
  [:nav {:class "top-bar" :data-topbar true}
   [:ul.title-area
    [:li.name
     [:h1 [:a {:href "#"} "Liburuak"]]]
    [:li {:class "toggle-topbar menu-icon"}
     [:a {:href "#"} [:span "menu"]]]]
   [:section.top-bar-section
    (if (:hasita @saioa)
      [:ul.right
       [:li.divider]
       [:li [:a {:href "#/bilatu"} "Bilatu"]]
       [:li.divider]
       [:li [:a {:href "#/liburua-gehitu"}
             "Liburua gehitu"]]
       [:li.divider]
       [:li [:a {:href "#/nire-liburuak"}
             "Nire liburuak"]]
       [:li.divider]
       [:li [:a {:href "#/nire-iruzkinak"}
             "Nire iruzkinak"]]       
       [:li.divider]
       [:li [:a {:href "#/profila"}
             "Nire profila"]]
       [:li.divider]
       [:li [:a {:href "#" :on-click #(do (put! saio-kan [:saioa-amaitu])
                                          false)}
             (str (:erabiltzailea @saioa)) "-ren saioa amaitu"]]]
      [:ul.right
       [:li.divider]
       [:li [:a {:href "#/bilatu"} "Bilatu"]]
       [:li.divider]
       [:li [:a {:href "#/erregistratu"} "Erregistratu"]]
       [:li.divider]
       [:li [:a {:href "#/saioa-hasi"} "Saioa hasi"]]])]])

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

(defn erregistratu [kan]
  (let [erabiltzailea (atom "")
        pasahitza (atom "")
        izena (atom "")
        deskribapena (atom "")]
    (fn [kan]
      [:div
       [:h2 "Erregistratu"]
       [:form
        [:label "Erabiltzaile izena"
         [:input {:type "text" :on-change #(reset! erabiltzailea (-> % .-target .-value))}]]
        [:label "Pasahitza"
         [:input {:type "password" :on-change #(reset! pasahitza (-> % .-target .-value))}]]
        [:label "Izena"
         [:input {:type "text" :on-change #(reset! izena (-> % .-target .-value))}]]
        [:label "Deskribapena"
         [:textarea {:rows "4" :on-change #(reset! deskribapena (-> % .-target .-value))}]]
        [:a.button {:href "#" :on-click #(put! kan [:erregistratu {:erabiltzailea @erabiltzailea
                                                                   :pasahitza @pasahitza
                                                                   :izena @izena
                                                                   :deskribapena @deskribapena}])}
         "Erregistratu"]]])))

(defn saioa-hasi [kan]
  (let [era (atom "")
        pas (atom "")]
    (fn [kan]
      [:div
       [:h2 "Saioa hasi"]
       [:form
        [:label "Erabiltzaile izena"
         [:input {:type "text" :on-change #(reset! era (-> % .-target .-value))}]]
        [:label "Pasahitza"
         [:input {:type "password" :on-change #(reset! pas (-> % .-target .-value))}]]
        [:a.button {:href "#" :on-click #(put! kan [:saioa-hasi {:era @era :pas @pas}])}
         "Saioa hasi"]]])))

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
        [:form
         [:label "Pasahitza"
          [:input {:type "password" :on-change #(reset! pas (-> % .-target .-value))}]]
         [:label "Izena"
          [:input {:type "text" :on-change #(reset! izen (-> % .-target .-value))}]]
         [:label "Deskribapena"
          [:textarea {:rows "4" :on-change #(reset! des (-> % .-target .-value))}]]
         [:a.button {:href "#" :on-click #(do (put! saio-kan [:erabiltzailea-aldatu {:era (:erabiltzailea @saioa)
                                                                                     :pas @pas
                                                                                     :izen @izen
                                                                                     :des @des}])
                                              false)}
          "Aldatu"]]
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
      [:form
       [:label "Edukia"]
       [:input {:type "text" :on-change #(reset! edukia (-> % .-target .-value))}]
       [:button {:on-click #(put! iruzkin-kan [:iruzkina-gehitu {:id libid
                                                                 :edukia {:edukia @edukia}}])}
        "Bidali"]])))

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

(defn liburua-gehitu [kan]
  (let [epub (atom "")
        titulua (atom "")
        egileak (atom "")
        hizkuntza (atom "")
        sinopsia (atom "")
        argitaletxea (atom "")
        urtea (atom "")
        generoa (atom "")
        etiketak (atom "")
        azala (atom "")
        azala-img-aldatu (fn [f]
                           (let [fr (js/FileReader.)]
                             (js/console.log fr)
                             (set! (.-onload fr)
                                   (fn [ger]
                                     (reset! azala (subs (.-result (.-target ger)) (count "data:image/jpeg;base64,")))
                                     (set! (.-src (js/document.getElementById "liburua-gehitu-azala-img"))
                                           (.-result (.-target ger)))))
                             (.readAsDataURL fr f)))
        azala-lortu (fn [tar]
                      (let [fitx (.item (.-files tar) 0)]
                        (js/console.log fitx)
                        (azala-img-aldatu fitx)))]
    (fn [kan]
      [:div
       [:h1 "Liburua gehitu"]
       [:form
        [:label "Epub"]
        [:input {:type "text" :on-change #(reset! epub (-> % .-target .-value))}]        
        [:label "Titulua"]
        [:input {:type "text" :on-change #(reset! titulua (-> % .-target .-value))}]
        [:label "Egileak"]
        [:input {:type "text" :on-change #(reset! egileak (-> % .-target .-value))}]
        [:label "Hizkuntza"]
        [:input {:type "text" :on-change #(reset! hizkuntza (-> % .-target .-value))}]        
        [:label "Sinopsia"]
        [:textarea {:type "text" :rows "4" :on-change #(reset! sinopsia (-> % .-target .-value))}]        
        [:label "Argitaletxea"]
        [:input {:type "text" :on-change #(reset! argitaletxea (-> % .-target .-value))}]
        [:label "Urtea"]
        [:input {:type "text" :on-change #(reset! urtea (-> % .-target .-value))}]
        [:label "Generoa"]
        [:input {:type "text" :on-change #(reset! generoa (-> % .-target .-value))}]
        [:label "Etiketak"]        
        [:input {:type "text" :on-change #(reset! etiketak (-> % .-target .-value))}]
        [:label "Azala"]
        [:img {:src "img/liburua.jpg" :id "liburua-gehitu-azala-img" :width "256" :height "256"}]
        [:input {:type "file" :id "liburua-gehitu-azala" :on-change #(azala-lortu (-> % .-target))}]
        [:button {:on-click #(put! kan [:liburua-gehitu {:epub @epub
                                                         :titulua @titulua
                                                         :egileak ["todo" "egileak"]
                                                         :hizkuntza @hizkuntza
                                                         :sinopsia @sinopsia
                                                         :argitaletxea @argitaletxea
                                                         :urtea @urtea
                                                         :generoa @generoa
                                                         :etiketak ["todo" "etiketak"]
                                                         :azala @azala}])}
         "Gehitu"]]])))

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

(defn nagusia [{:keys [saio-kan saioa liburu-kan iruzkin-kan bidea aliburuak nliburuak niruzkinak lib lib-irak]}]
  (let [[bid bal] @bidea]
    [:div.medium-8.columns
     [:div.row
      (case bid
        :index [azken-liburuak aliburuak]
        :erregistratu [erregistratu saio-kan]
        :saioa-hasi [saioa-hasi saio-kan]
        :profila [profila {:saio-kan saio-kan
                           :saioa saioa}]
        :liburua-gehitu [liburua-gehitu liburu-kan]
        :nire-liburuak [nire-liburuak liburu-kan nliburuak]
        :nire-iruzkinak [nire-iruzkinak iruzkin-kan niruzkinak]
        :liburua [liburua {:saioa saioa
                           :iruzkin-kan iruzkin-kan
                           :lib lib
                           :irak lib-irak}]
        :bilatu [bilatu]
        nil)]]))

(defn erdia [{:keys [saio-kan saioa liburu-kan iruzkin-kan bidea azken-iruzkinak aliburuak nliburuak niruzkinak liburua lib-irak]}]
  [:div.row
   [alboko-barra azken-iruzkinak]
   [nagusia {:saio-kan saio-kan
             :saioa saioa
             :liburu-kan liburu-kan
             :iruzkin-kan iruzkin-kan
             :bidea bidea
             :aliburuak aliburuak
             :nliburuak nliburuak
             :niruzkinak niruzkinak
             :lib liburua
             :lib-irak lib-irak}]])

(defn oina []
  [:footer.row
   [:div.medium-12.columns
    [:div.row
     [:div.medium-6.columns
      [:p "Oineko mezua"]]
     [:div.medium-6.columns
      [:ul.inline-list.right
       [:li [:a {:href "#"} "Lotura 1"]]
       [:li [:a {:href "#"} "Lotura 2"]]
       [:li [:a {:href "#"} "Lotura 3"]]]]]]])

(defn main [{:keys [saio-kan liburu-kan iruzkin-kan saioa bidea azken-iruzkinak aliburuak nliburuak niruzkinak liburua lib-irak]}]
  [:div {:class "row"}
   [:div {:class "medium-12 columns"}
    [goiko-barra {:saio-kan saio-kan
                  :saioa saioa}]
    [erdia {:saio-kan saio-kan
            :saioa saioa
            :liburu-kan liburu-kan
            :iruzkin-kan iruzkin-kan
            :bidea bidea
            :azken-iruzkinak azken-iruzkinak
            :aliburuak aliburuak
            :nliburuak nliburuak
            :niruzkinak niruzkinak
            :liburua liburua
            :lib-irak lib-irak}]
    [oina]]])
