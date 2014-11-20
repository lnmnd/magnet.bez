(ns magnet.bistak.goiko-barra
  (:require [cljs.core.async :refer [put!]]
            [reagent.core :as reagent :refer [atom]]))

(defn main [{:keys [saio-kan saioa]}]
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
