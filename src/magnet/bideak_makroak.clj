(ns magnet.bideak.makroak)

(defmacro bideak-definitu [& bideak]
  (cons 'do
        (map (fn [[ gak bid par gor]]
               `(secretary.core/defroute ~bid ~par
                  (cljs.core.async/put! magnet.bideak/kan [~gak ~gor])))
             bideak)))
