(ns magnet.bistak.lagun)

(defn- formu-ez-bidali [formu]
  (.addEventListener formu "submit"
                     (fn [ger]
                       (.preventDefault ger)
                       false)))

(defn- baliozko-formu? [formu]
  (or (not (.-checkValidity formu))
      (.checkValidity formu)))

(defn formu-tratatu [sel f]
  (let [formu (.querySelector js/document sel)]
    (formu-ez-bidali formu)
    (when (baliozko-formu? formu)
      (f))))
