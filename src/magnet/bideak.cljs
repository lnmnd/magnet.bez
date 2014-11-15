(ns magnet.bideak
  (:require [secretary.core :as secretary :refer-macros [defroute]]
            [goog.events :as events]
            [goog.history.EventType :as EventType]
            [cljs.core.async :refer [chan put!]])
  (:import goog.History))

(defn bideak-definitu []
  (let [kan (chan)]

    (secretary/set-config! :prefix "#")

    (defroute "/" []
      (put! kan [:index nil]))

    (defroute "/bilatu" []
      (put! kan [:bilatu nil]))

    (defroute "/erregistratu" []
      (put! kan [:erregistratu nil]))

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
    kan))
