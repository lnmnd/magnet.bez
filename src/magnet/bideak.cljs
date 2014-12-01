(ns magnet.bideak
  (:require [secretary.core :as secretary :refer-macros [defroute]]
            [goog.events :as events]
            [goog.history.EventType :as EventType]
            [cljs.core.async :refer [chan put!]]
            [magnet.makroak :refer-macros [bideak-eraiki]])
  (:import goog.History))

(defn bideak-definitu []
  (bideak-eraiki
   [:index "/"]
   [:bilatu "/bilatu"]
   [:erregistratu "/erregistratu"]
   [:saioa-hasi "/saioa-hasi"]
   [:liburua-gehitu "/liburua-gehitu"]
   [:nire-liburuak "/nire-liburuak"]
   [:nire-iruzkinak "/nire-iruzkinak"]
   [:nire-gogokoak "/nire-gogokoak"]
   [:profila "/profila"]
   [:liburua "/liburuak/:id" {:as params} (:id params)]))
