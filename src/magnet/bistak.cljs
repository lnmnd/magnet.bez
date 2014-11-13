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
                  [:img {:src "img/liburua.jpg"}]
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
    [:img {:src "img/liburua.jpg"}]]
   [:div.small-12.medium-12.columns
    [liburuaren-iruzkinak irak]
    (when (:hasita @saioa)
      [iruzkin-form {:iruzkin-kan iruzkin-kan
                     :libid (:id @lib)}])]])

(defn liburua-gehitu []
  (let [titulua (atom "")
        sinopsia (atom "")]
    (fn []
      [:div
       [:h1 "Liburua gehitu"]
       [:form
        [:label "Titulua"]
        [:input {:type "text" :on-change #(reset! titulua (-> % .-target .-value))}]
        [:label "Sinopsia"]
        [:textarea {:type "text" :rows "4" :on-change #(reset! sinopsia (-> % .-target .-value))}]
        [:button {:on-click #(println @titulua " liburua gehitu")} "Gehitu"]]])))

(defn bilatu []
  [:div
   [:h1 "Bilatu"]
   [:p "todo"]])

(defn nagusia [{:keys [saio-kan saioa iruzkin-kan bidea aliburuak lib lib-irak]}]
  (let [[bid bal] @bidea]
    [:div.medium-8.columns
     [:div.row
      (case bid
        :index [azken-liburuak aliburuak]
        :erregistratu [erregistratu saio-kan]
        :saioa-hasi [saioa-hasi saio-kan]
        :profila [profila {:saio-kan saio-kan
                           :saioa saioa}]
        :liburua-gehitu [liburua-gehitu]
        :nire-liburuak "todo nire liburuak"
        :liburua [liburua {:saioa saioa
                           :iruzkin-kan iruzkin-kan
                           :lib lib
                           :irak lib-irak}]
        :bilatu [bilatu]
        nil)]]))

(defn erdia [{:keys [saio-kan saioa iruzkin-kan bidea azken-iruzkinak aliburuak liburua lib-irak]}]
  [:div.row
   [alboko-barra azken-iruzkinak]
   [nagusia {:saio-kan saio-kan
             :saioa saioa
             :iruzkin-kan iruzkin-kan
             :bidea bidea
             :aliburuak aliburuak
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

(defn main [{:keys [saio-kan iruzkin-kan saioa bidea azken-iruzkinak aliburuak liburua lib-irak]}]
  [:div {:class "row"}
   [:div {:class "medium-12 columns"}
    [goiko-barra {:saio-kan saio-kan
                  :saioa saioa}]
    [erdia {:saio-kan saio-kan
            :saioa saioa
            :iruzkin-kan iruzkin-kan
            :bidea bidea
            :azken-iruzkinak azken-iruzkinak
            :aliburuak aliburuak
            :liburua liburua
            :lib-irak lib-irak}]
    [oina]]])
