(ns magnet.bistak.oina
  (:require [cljs.core.async :refer [put!]]
            [reagent.core :as reagent :refer [atom]]))

(defn main []
  [:footer.row
   [:div.medium-12.columns
    [:div.row
     [:div.medium-6.columns
      [:p "Liburutruk"]]
     [:div.medium-6.columns
      [:ul.inline-list.right
       [:li [:a {:href "#/honi-buruz"} "Honi buruz"]]]]]]])
