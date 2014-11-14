(ns magnet.bideak
  (:require [secretary.core :as secretary :refer-macros [defroute]]
            [goog.events :as events]
            [goog.history.EventType :as EventType]
            [cljs.core.async :refer [chan put!]]
            [magnet.bideak.makroak :as makroak :refer-macros [bideak-definitu]])
  (:import goog.History))

(def kan (chan))

(secretary/set-config! :prefix "#")

(bideak-definitu
 [:index "/" [] nil]
 [:bilatu "/bilatu" [] nil]
 [:erregistratu "/erregistratu" [] nil])

(defroute "/saioa-hasi" []
  (put! kan [:saioa-hasi nil]))

(defroute "/liburua-gehitu" []
  (put! kan [:liburua-gehitu nil]))

(defroute "/nire-liburuak" []
  (put! kan [:nire-liburuak nil]))

(defroute "/profila" []
  (put! kan [:profila nil]))

(defroute "/liburuak/:id" {:as params}
  (put! kan [:liburua (:id params)]))

(let [history (History.)]
  (events/listen history EventType/NAVIGATE
                 #(secretary/dispatch! (.-token %)))
  (.setEnabled history true))
