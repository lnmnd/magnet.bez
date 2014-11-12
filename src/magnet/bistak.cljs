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

(defn goiko-barra [kan saioa]
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
       [:li [:a {:href "#" :on-click #(do (put! kan [:saioa-amaitu])
                                          false)}
             (str (:erabiltzailea @saioa)) "-ren saioa amaitu"]]]
      [:ul.right
       [:li.divider]
       [erregistratu]
       [:li.divider]
       [saioa-hasi kan]])]])

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
   [:img {:src "http://placehold.it/256x256&text=Azala"}]
   [:div.panel
    [:h3 "Titulua"]
    [:h5 {:class "subheader"} "Liburu garrantzitsu baten datuak."]]
   [azken-iruzkinak iruzkinak]])

(defn azken-liburuak [libuk]
  [:div
   (for [li @libuk]
     ^{:key li} [:a {:href (str "#/liburuak/" (:id li))}
                 [:div.small-6.medium-4.columns
                  [:img {:src "http://placehold.it/256x256&text=Azala"}]
                  [:div.panel
                   [:h5 (:titulua li)]
                   [:h6.subheader "Datuak"]]]])])

(defn liburua [lib]
  [:div.row
   [:div.small-12.medium-6.columns
    [:h1 (:titulua @lib)]
    [:a {:href (:magnet @lib)} (:magnet @lib)]
    [:p "Egileak: "(for [e (:egileak @lib)]
                     [:span e " "])]
    [:p "Urtea" (:urtea @lib)]
    [:p (:sinopsia @lib)]]
   [:div.small-12.medium-6.columns
    [:img.right {:src "http://placehold.it/256x256&text=Azala"}]]
   [:h2 "Iruzkinak"]])

(defn nagusia [bidea aliburuak lib]
  (println @bidea)
  (let [[bid bal] @bidea]
    [:div.medium-8.columns
     [:div.row
      (case bid
        :index [azken-liburuak aliburuak]
        :liburua-gehitu "todo liburua gehitu"
        :nire-liburuak "todo nire liburuak"
        :liburua [liburua lib]
        nil)]]))

(defn erdia [bidea azken-iruzkinak aliburuak liburua]
  [:div.row
   [alboko-barra azken-iruzkinak]
   [nagusia bidea aliburuak liburua]])

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

(defn main [saio-kan saioa bidea azken-iruzkinak aliburuak liburua]
  [:div {:class "row"}
   [:div {:class "medium-12 columns"}
    [goiko-barra saio-kan saioa]
    [erdia bidea azken-iruzkinak aliburuak liburua]
    [oina]]])
