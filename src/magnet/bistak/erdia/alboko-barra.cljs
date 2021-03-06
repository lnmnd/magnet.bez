(ns magnet.bistak.erdia.alboko-barra)

(defn data [d]
  [:span (take 10 d) " " (take 8 (drop 11 d))])

(defn azken-iruzkina [ir]
  [:a {:href (str "#/liburuak/" (:liburua ir))}
   [:blockquote {:style {:font-size "smaller"}} (:edukia ir) [:cite (:erabiltzailea ir) " - " (data (:data ir))]]])

(defn azken-iruzkinak [iruzkinak]
  [:div
   [:h3 "Azken iruzkinak"
    (if (empty? @iruzkinak)
      [:p "Iruzkinik ez."]
      (for [ir @iruzkinak]
        ^{:key ir} [azken-iruzkina ir]))]])

(defn gogoko-liburua [lib]
  [:div
   [:a {:href (str "#/liburuak/" (:id @lib))}
    [:img {:src (:azala @lib)}]]
   [:div.panel
    [:h3 (:titulua @lib)]
    [:h5 {:class "subheader"} (for [x (interpose ", " (:egileak @lib))]
                                [:span x])]
    [:p (:iruzkin_kopurua @lib) " iruzkin" [:br]
     (:gogoko_kopurua @lib) " gogoko"]]])

(defn alboko-barra
  "Liburu bat eta iruzkinak erakusten ditu."
  [lib iruzkinak]
  [:div.medium-4.small-12.hide-for-small.columns
   (when (> (:gogoko_kopurua @lib) 0)
     [gogoko-liburua lib])
   [azken-iruzkinak iruzkinak]])
