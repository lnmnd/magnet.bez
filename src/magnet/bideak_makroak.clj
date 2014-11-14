(ns magnet.bideak.makroak)

(defmacro bidea [gak bid par gor]
  `(secretary.core/defroute ~bid ~par
     (cljs.core.async/put! magnet.bideak/kan [~gak ~gor])))
