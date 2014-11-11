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
(defn saioa-hasi [kon]
  [:li
   [:a {:href "#" :data-reveal-id-ez-erabili "saioaHasiModal"
        :on-click #(put! kon [:saioa-hasi {:era "era" :pas "1234"}])} "Saioa hasi"]
   [:div.reveal-modal.remove-whitespace {:id "saioaHasiModal" :data-reveal true}
    [:h2 "Saioa hasi"]
    "TODO"
    [:a.close-reveal-modal "X"]]])

(defn goiko-barra [kon saioa]
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
       [:li [:a {:href "#" :on-click #(put! kon [:saioa-amaitu])}
             (str (:erabiltzailea @saioa)) "-ren saioa amaitu"]]]
      [:ul.right
       [:li.divider]
       [erregistratu]
       [:li.divider]
       [saioa-hasi kon]])]])

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
  [:div.large-4.small-12.columns
   [:img {:src "http://placehold.it/500x500&text=Azala"}]
   [:div.hide-for-small.panel
    [:h3 "Titulua"]
    [:h5 {:class "subheader"} "Liburu garrantzitsu baten datuak."]]
   [azken-iruzkinak iruzkinak]])

(defn nagusia [bidea]
  (let [[bid bal] @bidea]
    [:div.large-8.columns
     [:div.row
      (case bid
        :index "todo index"
        :liburua-gehitu "todo liburua gehitu"
        :nire-liburuak "todo nire liburuak"
        :liburua "todo lib"
        nil)]]))

(defn erdia [bidea azken-iruzkinak]
  [:div.row
   [alboko-barra azken-iruzkinak]
   [nagusia bidea]])

(defn oina []
  [:footer.row
   [:div.large-12.columns
    [:div.row
     [:div.large-6.columns
      [:p "Oineko mezua"]]
     [:div.large-6.columns
      [:ul.inline-list.right
       [:li [:a {:href "#"} "Lotura 1"]]
       [:li [:a {:href "#"} "Lotura 2"]]
       [:li [:a {:href "#"} "Lotura 3"]]]]]]])

(defn main [saio-kon saioa bidea azken-iruzkinak]
  [:div {:class "row"}
   [:div {:class "large-12 columns"}
    [goiko-barra saio-kon saioa]
    [erdia bidea azken-iruzkinak]
    [oina]]])
