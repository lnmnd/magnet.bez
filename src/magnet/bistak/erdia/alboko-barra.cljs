(ns magnet.bistak.erdia.alboko-barra)

(defn azken-iruzkina [ir]
  [:a {:href (str "#/liburuak/" (:liburua ir))}
   [:div.panel.radius
    [:blockquote {:style {:font-size "smaller"}} (:edukia ir) [:cite (:erabiltzailea ir) " - " (:data ir)]]]])

(defn azken-iruzkinak [iruzkinak]
  [:div
   [:h3 "Azken iruzkinak"
    (for [ir @iruzkinak]
      ^{:key ir} [azken-iruzkina ir])]])

(defn alboko-barra
  "Liburu bat eta iruzkinak erakusten ditu."
  [lib iruzkinak]
  [:div.medium-4.small-12.hide-for-small.columns
   [:a {:href (str "#/liburuak/" (:id @lib))}
    [:img {:src (:azala @lib)}]]
   [:div.panel
    [:h3 (:titulua @lib)]
    [:h5 {:class "subheader"} (for [x (interpose ", " (:egileak @lib))]
                                [:span x])]
    [:p (:iruzkin_kopurua @lib) " iruzkin" [:br]
     (:gogoko_kopurua @lib) " gogoko"]]
   [azken-iruzkinak iruzkinak]])
