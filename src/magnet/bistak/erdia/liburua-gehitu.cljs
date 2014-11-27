(ns magnet.bistak.erdia.liburua-gehitu
  (:require [cljs.core.async :refer [put!]]
            [reagent.core :as reagent :refer [atom]]
            [magnet.bistak.lagun :refer [formu-tratatu]]))

(defn liburua-gehitu [kan egile-guztiak argitaletxeak generoak etiketa-guztiak]
  (let [bidali-klikatuta (atom false)
        epub (atom "")
        epub-aukeratuta (atom false)
        epub-fitxategia? (atom false)
        epub-edukia-aldatu (fn [f]
                             (let [fr (js/FileReader.)]
                               (set! (.-onload fr)
                                     (fn [ger]
                                       (let [mota "data:application/epub+zip;base64,"]
                                         (reset! epub-aukeratuta true)
                                         (reset! epub-fitxategia?
                                                 (= mota (clojure.string/join (take (count mota) (.-result (.-target ger))))))
                                         (reset! epub (subs (.-result (.-target ger)) (count mota))))))
                               (.readAsDataURL fr f)))
        epub-lortu (fn [tar]
                     (let [fitx (.item (.-files tar) 0)]
                       (epub-edukia-aldatu fitx)))
        titulua (atom "")
        egileak (atom (sorted-map))
        egile-kop (atom 0)
        egilea-gehitu (fn [egilea]
                        (let [id (swap! egile-kop inc)]
                          (swap! egileak assoc id {:id id :egilea egilea})))
        egilea-ezabatu #(swap! egileak dissoc %)
        egile-zerrenda (fn [egak]
                         (map #(:egilea (second %)) egak))
        egilerik-gehituta #(> (count (egile-zerrenda @egileak)) 0)
        hizkuntza (atom "")
        sinopsia (atom "")
        argitaletxea (atom "")
        urtea (atom "")
        generoa (atom "")
        etiketak (atom (sorted-map))
        etiketa-kop (atom 0)
        etiketa-gehitu (fn [etiketa]
                         (let [id (swap! etiketa-kop inc)]
                           (swap! etiketak assoc id {:id id :etiketa etiketa})))
        etiketa-ezabatu #(swap! etiketak dissoc %)
        etiketa-zerrenda (fn [etik]
                           (map #(:etiketa (second %)) etik))        
        azala (atom "")
        azala-aukeratuta (atom false)
        jpg-azala (atom false)
        azala-img-aldatu (fn [f]
                           (let [fr (js/FileReader.)]
                             (set! (.-onload fr)
                                   (fn [ger]
                                     (let [mota "data:image/jpeg;base64,"]
                                       (reset! azala-aukeratuta true)
                                       (reset! jpg-azala
                                               (= mota (clojure.string/join (take (count mota) (.-result (.-target ger))))))
                                       (reset! azala (subs (.-result (.-target ger)) (count mota)))
                                       (set! (.-src (js/document.getElementById "liburua-gehitu-azala-img"))
                                             (.-result (.-target ger))))))
                             (.readAsDataURL fr f)))
        azala-lortu (fn [tar]
                      (let [fitx (.item (.-files tar) 0)]
                        (azala-img-aldatu fitx)))
        formu-zuzena (fn [] (and @epub-fitxategia? (egilerik-gehituta) @jpg-azala))]
    (fn [kan egile-guztiak argitaletxeak generoak etiketa-guztiak]
      [:div
       [:h1 "Liburua gehitu"]
       [:form {:id "liburua-gehitu"}
        [:label "Epub"]
        [:input {:type "file" :required true :on-change #(epub-lortu (-> % .-target))}]
        (when (and @epub-aukeratuta (not @epub-fitxategia?))
          [:small.error "Fitxategiak epub formatua eduki behar du."])
        [:label "Titulua"]
        [:input {:type "text" :required true :max-length "256" :on-change #(reset! titulua (-> % .-target .-value))}]
        [:label "Egileak"]
        [:div.row.collapse
         [:div.small-10.columns
          [:input {:type "text" :id "egilea" :placeholder "egile1,egile2" :list "liburua-gehitu-egileak"}]
          [:datalist {:id "liburua-gehitu-egileak"}
           (for [x @egile-guztiak]
             [:option {:value x}])]]
         [:div.small-2.columns
          [:a.button.postfix {:on-click #(do (let [egileak (clojure.string.split (.-value (.querySelector js/document "#egilea")) #",")]
                                               (doseq [x egileak]
                                                 (egilea-gehitu x))) 
                                             (set! (.-value (.querySelector js/document "#egilea")) ""))}
           "Gehitu"]]]
        (when (and @bidali-klikatuta (not (egilerik-gehituta)))
          [:small.error "Egile bat gutxienez gehitu."])
        [:ul
         (for [e @egileak]
           [:li [:a {:on-click #(egilea-ezabatu (:id (second e)))} "X"] " " (:egilea (second e))])]
        [:label "Hizkuntza"]
        [:input {:type "text" :required true :max-length "256" :list "liburua-gehitu-hizkuntzak" :on-change #(reset! hizkuntza (-> % .-target .-value))}]
        [:datalist {:id "liburua-gehitu-hizkuntzak"}
         [:option {:value "Euskara"}]
         [:option {:value "Gaztelania"}]
         [:option {:value "Frantsesa"}]
         [:option {:value "Ingelesa"}]]
        [:label "Sinopsia"]
        [:textarea {:type "text" :required true :rows "4" :max-length "256" :on-change #(reset! sinopsia (-> % .-target .-value))}]        
        [:label "Argitaletxea"]
        [:input {:type "text" :max-length "256" :list "liburua-gehitu-argitaletxeak" :on-change #(reset! argitaletxea (-> % .-target .-value))}]
        [:datalist {:id "liburua-gehitu-argitaletxeak"}
         (for [a @argitaletxeak]
           [:option {:value a}])]
        [:label "Urtea"]
        [:input {:type "number" :required true :max-length "4" :on-change #(reset! urtea (-> % .-target .-value))}]
        [:label "Generoa"]
        [:input {:type "text" :max-length "256" :list "liburua-gehitu-generoak" :on-change #(reset! generoa (-> % .-target .-value))}]
        [:datalist {:id "liburua-gehitu-generoak"}
         (for [x @generoak]
           [:option {:value x}])]
        [:label "Etiketak"]
        [:div.row.collapse
         [:div.small-10.columns
          [:input {:type "text" :id "etiketa" :placeholder "etiketa1,etiketa2" :list "liburua-gehitu-etiketak"}]
          [:datalist {:id "liburua-gehitu-etiketak"}
           (for [x @etiketa-guztiak]
             [:option {:value x}])]]
         [:div.small-2.columns
          [:a.button.postfix {:on-click #(do (let [etiketak (clojure.string.split (.-value (.querySelector js/document "#etiketa")) #",")]
                                               (doseq [x etiketak]
                                                 (etiketa-gehitu x)))
                                             (set! (.-value (.querySelector js/document "#etiketa")) ""))}
           "Gehitu"]]]
        [:ul
         (for [e @etiketak]
           [:li [:a {:on-click #(etiketa-ezabatu (:id (second e)))} "X"] " " (:etiketa (second e))])]        
        [:label "Azala"]
        [:img {:src "img/liburua.jpg" :id "liburua-gehitu-azala-img" :width "256" :height "256"}]
        [:input {:type "file" :required true :id "liburua-gehitu-azala" :on-change #(azala-lortu (-> % .-target))}]
        (when (and @azala-aukeratuta (not @jpg-azala))
          [:small.error "Azalak JPG formatua eduki behar du."])
        [:input.button {:type "submit" :value "Gehitu"
                        :on-click (fn []
                                    (reset! bidali-klikatuta true)
                                    (when (formu-zuzena)
                                      (formu-tratatu "#liburua-gehitu"
                                                     #(put! kan [:liburua-gehitu {:epub @epub
                                                                                  :titulua @titulua
                                                                                  :egileak (egile-zerrenda @egileak)
                                                                                  :hizkuntza @hizkuntza
                                                                                  :sinopsia @sinopsia
                                                                                  :argitaletxea @argitaletxea
                                                                                  :urtea @urtea
                                                                                  :generoa @generoa
                                                                                  :etiketak (etiketa-zerrenda @etiketak)
                                                                                  :azala @azala}]))))}]]])))
