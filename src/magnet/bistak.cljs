(ns magnet.bistak)

(defn saioa-hasi-modal []
  [:div {:id "saioaHasiModal" :class "reveal-modal" "remove-whitespace" :data-reveal}
   "TODO"])

(defn goiko-barra []
  [:nav {:class "top-bar" :data-topbar true}
   [:ul.title-area
    [:li.name
     [:h1 [:a {:href "#"} "Liburuak"]]]
    [:li {:class "toggle-topbar menu-icon"}
     [:a {:href "#"} [:span "menu"]]]]
   [:section.top-bar-section
    [:ul.right
     [:li.divider]
     [:li
      [:a {:href "#" :data-reveal-id "saioaHasiModal"} "Saioa hasi"]]]]
   [saioa-hasi-modal]])

(defn main []
  [:div {:class "row"}
   [:div {:class "large-12 columns"}
    [goiko-barra]]])
