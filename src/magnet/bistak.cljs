(ns magnet.bistak)

(defn saioa-hasi []
  [:li
   [:a {:href "#" :data-reveal-id "saioaHasiModal"} "Saioa hasi"]
   [:div {:id "saioaHasiModal" :class "reveal-modal" "remove-whitespace" :data-reveal}
    "TODO"]])

(defn goiko-barra [saioa]
  [:nav {:class "top-bar" :data-topbar true}
   [:ul.title-area
    [:li.name
     [:h1 [:a {:href "#"} "Liburuak"]]]
    [:li {:class "toggle-topbar menu-icon"}
     [:a {:href "#"} [:span "menu"]]]]
   [:section.top-bar-section
    [:ul.right
     [:li.divider]
     (if (:hasita @saioa)
       [:li [:a {:href "#"} "Saioa amaitu"]]
       [saioa-hasi])]]])

(defn main [saioa]
  [:div {:class "row"}
   [:div {:class "large-12 columns"}
    [goiko-barra saioa]]])
