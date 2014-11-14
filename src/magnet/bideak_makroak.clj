(ns magnet.bideak.makroak)

(defn bidea-eraiki [[gak bid par gor]]
  `(secretary.core/defroute ~bid ~par
     (cljs.core.async/put! magnet.bideak/kan [~gak ~gor])))

(defmacro bideak-definitu [& bideak]
  (cons 'do (map bidea-eraiki bideak)))
