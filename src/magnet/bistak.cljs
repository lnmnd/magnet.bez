(ns magnet.bistak
  (:require [cljs.core.async :refer [put!]]))

(defn erregistratu []
  [:li [:a {:href "#" :data-reveal-id "erregistratuModal"} "Erregistratu"]
   [:div.reveal-modal {:id "erregistratuModal" :data-reveal true}
    [:h2 "Erregistratu"]
    [:form
     [:label "Erabiltzaile izena"
      [:input {:type "text"}]]
     [:label "Pasahitza"
      [:input {:type "password"}]]
     [:label "Izena"
      [:input {:type "text"}]]
     [:a.button {:href "#"} "Erregistratu"]]
    [:a.close-reveal-modal "X"]]])

; TODO saioa hasteko formu
(defn saioa-hasi [kan]
  [:li
   [:a {:href "#" :data-reveal-id-ez-erabili "saioaHasiModal"
        :on-click #(do (put! kan [:saioa-hasi {:era "era" :pas "1234"}])
                       false)} "Saioa hasi"]
   [:div.reveal-modal.remove-whitespace {:id "saioaHasiModal" :data-reveal true}
    [:h2 "Saioa hasi"]
    "TODO"
    [:a.close-reveal-modal "X"]]])

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
       [:li [:a {:href "#/liburua-gehitu"}
             "Liburua gehitu"]]
       [:li.divider]
       [:li [:a {:href "#/nire-liburuak"}
             "Nire liburuak"]]
       [:li.divider]
       [:li [:a {:href "#" :on-click #(do (put! saio-kan [:saioa-amaitu])
                                          false)}
             (str (:erabiltzailea @saioa)) "-ren saioa amaitu"]]]
      [:ul.right
       [:li.divider]
       [erregistratu]
       [:li.divider]
       [saioa-hasi saio-kan]])]])

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

(defn azken-liburuak [libuk]
  [:div
   (for [li @libuk]
     ^{:key li} [:a {:href (str "#/liburuak/" (:id li))}
                 [:div.small-6.medium-4.columns
                  [:img {:src "img/liburua.jpg"}]
                  [:div.panel
                   [:h5 (:titulua li)]
                   [:h6.subheader (interpose ", " (:egileak li))]
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

(defn liburua [{:keys [iruzkin-kan lib irak]}]
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
    [iruzkin-form {:iruzkin-kan iruzkin-kan
                   :libid (:id @lib)}]]])

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
        [:button {:on-click #(println @titulua " liburua gehitu")} "Gehitu"]]
])))

(defn nagusia [{:keys [iruzkin-kan bidea aliburuak lib lib-irak]}]
  (let [[bid bal] @bidea]
    [:div.medium-8.columns
     [:div.row
      (case bid
        :index [azken-liburuak aliburuak]
        :liburua-gehitu [liburua-gehitu]
        :nire-liburuak "todo nire liburuak"
        :liburua [liburua {:iruzkin-kan iruzkin-kan
                           :lib lib
                           :irak lib-irak}]
        nil)]]))

(defn erdia [{:keys [iruzkin-kan bidea azken-iruzkinak aliburuak liburua lib-irak]}]
  [:div.row
   [alboko-barra azken-iruzkinak]
   [nagusia {:iruzkin-kan iruzkin-kan
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
    [erdia {:iruzkin-kan iruzkin-kan
            :bidea bidea
            :azken-iruzkinak azken-iruzkinak
            :aliburuak aliburuak
            :liburua liburua
            :lib-irak lib-irak}]
    [oina]]])
