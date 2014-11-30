(ns magnet.bideak.makroak)

(defmacro bideak-eraiki
  "Bideak eraiki eta kanal bat itzultzen du, non bidearen balioa gehitzen den."
  [& bideak]
  (let [kan (gensym "kan")]
    `(let [~kan (cljs.core.async/chan)]
       (secretary.core/set-config! :prefix "#")
       ~@(map (fn [x]
                (if (= (count x) 2)
                  `(secretary.core/defroute ~(nth x 1) []
                     (cljs.core.async/put! ~kan [~(nth x 0) nil]))
                  `(secretary.core/defroute ~(nth x 1) ~(nth x 2)
                     (cljs.core.async/put! ~kan [~(nth x 0) ~(nth x 3)]))))
              bideak)
       (let [history# (goog.History.)]
         (goog.events/listen history# goog.history.EventType/NAVIGATE
                             #(secretary.core/dispatch! (.-token %)))
         (.setEnabled history# true))
       ~kan)))
