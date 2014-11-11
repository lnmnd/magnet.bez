(ns magnet.bistak
  (:require [cljs.core.async :refer [put!]]))

; TODO saioa hasteko formu
(defn saioa-hasi [kon]
  [:li
   [:a {:href "#" :data-reveal-id-ez-erabili "saioaHasiModal"
        :on-click #(put! kon [:saioa-hasi {:era "era" :pas "1234"}])} "Saioa hasi"]
   [:div {:id "saioaHasiModal" :class "reveal-modal" "remove-whitespace" :data-reveal}
    "TODO"]])

(defn goiko-barra [kon saioa]
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
       [:li [:a {:href "#" :on-click #(put! kon [:saioa-amaitu])} "Saioa amaitu"]]
       [saioa-hasi kon])]]])

(defn main [saio-kon saioa]
  [:div {:class "row"}
   [:div {:class "large-12 columns"}
    [goiko-barra saio-kon saioa]]])
