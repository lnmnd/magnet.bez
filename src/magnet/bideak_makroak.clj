(ns magnet.bideak.makroak)

(defn bidea-eraiki
  ([gak bid]
     (bidea-eraiki gak bid [] nil))
  ([gak bid par gor]
     `(secretary.core/defroute ~bid ~par
        (cljs.core.async/put! magnet.bideak/kan [~gak ~gor]))))

(defmacro bideak-definitu [& bideak]
  (cons 'do (map (fn [x]
                   (if (= (count x) 2)
                     (bidea-eraiki (nth x 0) (nth x 1))
                     (bidea-eraiki (nth x 0) (nth x 1) (nth x 2) (nth x 3))))
                 bideak)))
