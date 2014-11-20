(ns magnet.bistak.oina
  (:require [cljs.core.async :refer [put!]]
            [reagent.core :as reagent :refer [atom]]))

(defn main []
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
