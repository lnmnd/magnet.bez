(ns magnet.bideak.makroak)

(defn bidea-eraiki
  [kan gak bid & [par gor]]
  `(secretary.core/defroute ~bid ~(or par [])
     (cljs.core.async/put! ~kan [~gak ~(or gor nil)])))

(defmacro bideak-eraiki
  "Bideak eraiki eta kanal bat itzultzen du, non bidearen balioa gehitzen den."
  [& bideak]
  (let [kan (gensym "kan")]
    `(let [~kan (cljs.core.async/chan)]
       (secretary.core/set-config! :prefix "#")
       
       ~@(map #(apply bidea-eraiki kan %) bideak)
       
       (let [history# (goog.History.)]
         (goog.events/listen history# goog.history.EventType/NAVIGATE
                             #(secretary.core/dispatch! (.-token %)))
         (.setEnabled history# true))
       
       ~kan)))
