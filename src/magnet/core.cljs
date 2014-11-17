(ns magnet.core
  (:require-macros [cljs.core.async.macros :refer [go go-loop]])
  (:require [cljs.core.async :as async :refer [chan put! <! >! timeout]]
            [ajax.core :refer [GET POST PUT DELETE]]
            [reagent.core :as reagent :refer [atom]]
            [figwheel.client :as fw]
            [magnet.config :refer [azken-iruzkin-kopurua azken-liburu-kopurua]]
            [magnet.bideak :as bideak]
            [magnet.bistak :as bistak]))

(enable-console-print!)

(def ^{:doc "APIaren aurrizkia"}
  aurriz "http://localhost:3000/v1/")

(defonce ^{:doc "Erabiltzailearen saioa"}
  saioa (atom {:hasita false
               :erabiltzailea nil
               :izena nil
               :deskribapena nil
               :token nil
               :iraungitze_data nil}))
(defonce ^{:doc "Uneko bidea"}
  bidea (atom []))
(defonce ^{:doc "Azken iruzkinen zerrenda"}
  azken-iruzkinak (atom []))
(defonce ^{:doc "Nire liburuen zerrenda"}
  nire-liburuak (atom []))
(defonce ^{:doc "Nik idatzitako iruzkinen zerrenda"}
  nire-iruzkinak (atom []))
(defonce ^{:doc "Azken liburuen zerrenda"}
  azken-liburuak (atom []))
(defonce ^{:doc "Liburuaren datuak"}
  liburua (atom {}))
(defonce ^{:doc "Uneko liburuaren iruzkinak"}
  liburuaren-iruzkinak (atom {}))

(defn entzun
  "Helbidea entzuten du eta erantzunari funtzioa aplikatzen dio.
  Emaitza duen kanala itzultzen du"
  ([helbidea f ef]
     (let [kan (chan)]
       (GET helbidea
            {:response-format :json
             :keywords? true
             :handler #(put! kan (f %))
             :error-handler #(put! kan (ef %))})
       kan))
  ([helbidea f]
     (let [kan (chan)]
       (GET helbidea
            {:response-format :json
             :keywords? true
             :handler #(put! kan (f %))})
       kan)))

(defn bidali-eta-entzun
  "Helbidera datuak bidaltzen ditu eta erantzunari funtzioa aplikatzen dio.
  Emaitza duen kanala itzultzen du"
  ([helbidea dat f]
     (let [kan (chan)]
       (POST helbidea
             {:params dat
              :format :json
              :response-format :json
              :keywords? true
              :handler #(put! kan (f %))})
       kan))
  ([helbidea dat f ef]
     (let [kan (chan)]
       (POST helbidea
             {:params dat
              :format :json
              :response-format :json
              :keywords? true
              :handler #(put! kan (f %))
              :error-handler #(put! kan (ef %))})
       kan)))

(defn aldatu-eta-entzun
  "Helbidera aldatzeko datuak bidaltzen ditu eta erantzunari funtzioa aplikatzen dio.
  Emaitza duen kanala itzultzen du"
  ([helbidea dat f]
     (let [kan (chan)]
       (PUT helbidea
            {:params dat
             :format :json
             :response-format :json
             :keywords? true
             :handler #(put! kan (f %))})
       kan))
  ([helbidea dat f ef]
     (let [kan (chan)]
       (PUT helbidea
            {:params dat
             :format :json
             :response-format :json
             :keywords? true
             :handler #(put! kan (f %))
             :error-handler #(put! kan (ef %))})
       kan)))

(defn erabiltzailea-gehitu
  "Erabiltzaile berri bat gehitzen du."
  ([era pas izen]
     (erabiltzailea-gehitu era pas izen ""))
  ([era pas izen desk]
     (let [param {:erabiltzailea era
                  :pasahitza pas
                  :izena izen}]
       (bidali-eta-entzun (str aurriz "erabiltzaileak")
                          (if (empty? desk) param (assoc param :deskribapena desk))
                          identity))))
#_(erabiltzailea-gehitu "era" "1234" "era")

(defn erabiltzailea-lortu [era]
  (entzun (str aurriz "erabiltzaileak/" era)
          :erabiltzailea))

(defn erabiltzailea-aldatu
  "Erabiltzaile berri bat gehitzen du."
  ([era pas izen]
     (erabiltzailea-aldatu era pas izen ""))
  ([era pas izen desk]
     (swap! saioa assoc :izena izen)
     (swap! saioa assoc :deskribapena desk)
     (let [param {:pasahitza pas
                  :izena izen}]
       (aldatu-eta-entzun (str aurriz "erabiltzaileak/" era "?token=" (:token @saioa))
                          (if (empty? desk) param (assoc param :deskribapena desk))
                          identity))))

(defn saioa-hasi
  "Saioa hasten du."
  [era pas]
  (bidali-eta-entzun
   (str aurriz "saioak")
   {:erabiltzailea era
    :pasahitza pas}
   #(go (let [dat (<! (erabiltzailea-lortu era))]
          (swap! saioa assoc
                 :hasita true
                 :erabiltzailea era
                 :izena (:izena dat)
                 :deskribapena (:deskribapena dat)
                 :token (:token %)
                 :iraungitze_data (:iraungitze_data %))))))
#_(saioa-hasi "era" "1234")

(defn saioa-amaitu
  "Saioa amaitzen du."
  []
  (DELETE (str aurriz "saioak/" (:token @saioa)))
  (reset! saioa {:hasita false
                 :erabiltzailea nil
                 :izena nil
                 :token nil
                 :iraungitze_data nil}))
#_(saioa-amaitu)

(defn erabiltzailea-ezabatu
  "Erabiltzailea ezabatu eta saioa amaitzen du."
  []
  (DELETE (str aurriz "erabiltzaileak/" (:erabiltzailea @saioa) "?token=" (:token @saioa))
          {:handler #(saioa-amaitu)
           :error-handler #(println %)}))
#_(erabiltzailea-ezabatu)

(defn liburua-gehitu [edukia]
  (bidali-eta-entzun (str aurriz "liburuak?token=" (:token @saioa))
                     edukia
                     :liburua))

(defn liburua-ezabatu [id]
  (DELETE (str aurriz "liburuak/" id "?token=" (:token @saioa))))

(defn iruzkina-ezabatu [id]
  (DELETE (str aurriz "iruzkinak/" id "?token=" (:token @saioa))))

(defn iruzkin-liburu-titulua
  [ir]
  (entzun (str aurriz "liburuak/" (:liburua ir))
          (fn [x]
            (assoc ir :liburu_titulua (:titulua (:liburua x))))))

(defn azken-iruzkinak-lortu
  "Azken iruzkinak lortzen ditu"
  ([]
     (entzun (str aurriz "iruzkinak")
             #(azken-iruzkinak-lortu (if (> (:guztira %) azken-iruzkin-kopurua)
                                       (- (:guztira %) azken-iruzkin-kopurua)
                                       0))))
  ([desp]
     (entzun (str aurriz "iruzkinak?desplazamendua=" desp "&muga=" azken-iruzkin-kopurua)
             #(do (reset! azken-iruzkinak (reverse (:iruzkinak %)))))))
#_(azken-iruzkinak-lortu)

(defn nire-liburuak-lortu []
  (go (let [libk (<! (entzun (str aurriz "erabiltzaileak/" (:erabiltzailea @saioa) "/liburuak")
                             :liburuak))]
        (reset! nire-liburuak (rseq libk)))))

(defn nire-iruzkinak-lortu []
  (go (let [irk (<! (entzun (str aurriz "erabiltzaileak/" (:erabiltzailea @saioa) "/iruzkinak?muga=0")
                            :iruzkinak))]
        (reset! nire-iruzkinak (rseq irk)))))

(defn azken-liburuak-lortu
  "Azken liburuak lortzen ditu"
  ([]
     (entzun (str aurriz "liburuak")
             #(azken-liburuak-lortu (if (> (:guztira %) azken-liburu-kopurua)
                                      (- (:guztira %) azken-liburu-kopurua)
                                      0))))
  ([desp]
     (entzun (str aurriz "liburuak?desplazamendua=" desp "&muga=" azken-liburu-kopurua)
             #(reset! azken-liburuak (reverse (:liburuak %))))))
#_(azken-liburuak-lortu)

(defn liburua-lortu
  "id duen liburua lortzen du."
  [id]
  (entzun (str aurriz "liburuak/" id)
          #(reset! liburua (:liburua %))))
#_(liburua-lortu 1)

(defn liburuaren-iruzkinak-lortu
  "id duen libururen iruzkinak lortzen ditu."
  [id]
  (entzun (str aurriz "liburuak/" id "/iruzkinak?muga=0")
          #(reset! liburuaren-iruzkinak (:iruzkinak %))))

(defn iruzkina-gehitu
  "id liburuari erantzuten dion iruzkina gehitzen du."
  [id edukia]
  (bidali-eta-entzun
   (str aurriz "liburuak/" id "/iruzkinak?token=" (:token @saioa))
   edukia
   identity))

(defn egile-guztiak-lortu
  "Egile guztien zerrenda lortzen du"
  []
  (entzun (str aurriz "egileak?muga=0") :egileak))
#_(go (println (<! (egile-guztiak-lortu))))

(defn saio-kud [[mota bal]]
  "Saioaren gertaerekin zer egin erabakitzen du"
  (case mota
    :erregistratu (go (<! (erabiltzailea-gehitu (:erabiltzailea bal) (:pasahitza bal) (:izena bal) (:deskribapena bal)))
                      (saioa-hasi (:erabiltzailea bal) (:pasahitza bal)))
    :erabiltzailea-aldatu (erabiltzailea-aldatu (:era bal) (:pas bal) (:izen bal) (:des bal))
    :erabiltzailea-ezabatu (erabiltzailea-ezabatu)
    :saioa-hasi (saioa-hasi (:era bal) (:pas bal))
    :saioa-amaitu (saioa-amaitu)
    nil))

(defn bide-kud [[mota bal]]
  "Bidearen gertaerekin zer egin erabakitzen du."
  (if (= :index mota)
    (azken-liburuak-lortu))
  (when (= :liburua mota)
    (liburua-lortu bal)
    (liburuaren-iruzkinak-lortu bal))
  (when (= :nire-liburuak mota)
    (nire-liburuak-lortu))
  (when (= :nire-iruzkinak mota)
    (nire-iruzkinak-lortu))
  (when (contains? #{:index :erregistratu :saioa-hasi :liburua-gehitu :nire-liburuak :nire-iruzkinak :profila :liburua :bilatu}
                   mota)
    (reset! bidea [mota bal])))

(defn liburu-kud [[mota bal] bide-kan]
  "Liburuekin lotutako kudeatzailea."
  (case mota
    :liburua-gehitu (go (let [li (<! (liburua-gehitu bal))]
                          (>! bide-kan [:liburua (:id li)])))
    :liburua-ezabatu (do (swap! nire-liburuak (fn [lk] (remove #(= bal (:id %)) lk)))
                         (liburua-ezabatu bal))
    nil))

(defn iruzkin-kud [[mota bal]]
  "Iruzkinekin lotutako kudeatzailea."
  (case mota
    :iruzkina-gehitu (do (swap! liburuaren-iruzkinak conj (:edukia bal))
                         (iruzkina-gehitu (:id bal) (:edukia bal)))
    :iruzkina-ezabatu (do (swap! nire-iruzkinak (fn [lk] (remove #(= bal (:id %)) lk)))
                          (iruzkina-ezabatu bal))
    nil))

(defn errendatu [{:keys [saio-kan liburu-kan iruzkin-kan]}]
  (reagent/render-component [bistak/main {:saio-kan saio-kan
                                          :liburu-kan liburu-kan
                                          :iruzkin-kan iruzkin-kan
                                          :saioa saioa
                                          :bidea bidea
                                          :azken-iruzkinak azken-iruzkinak
                                          :aliburuak azken-liburuak
                                          :nliburuak nire-liburuak
                                          :niruzkinak nire-iruzkinak
                                          :liburua liburua
                                          :lib-irak liburuaren-iruzkinak}]
                            (.querySelector js/document "#app")))

(defn ^:export run []
  (let [saio-kan (chan)
        bide-kan (bideak/bideak-definitu)
        liburu-kan (chan)
        iruzkin-kan (chan)]
    
    (errendatu {:saio-kan saio-kan
                :liburu-kan liburu-kan
                :iruzkin-kan iruzkin-kan})
        
    (go-loop [b (<! saio-kan)]
      (when b
        (saio-kud b)
        (recur (<! saio-kan))))

    (go-loop [b (<! bide-kan)]
      (when b
        (bide-kud b)
        (recur (<! bide-kan))))

    (go-loop [b (<! liburu-kan)]
      (when b
        (liburu-kud b bide-kan)
        (recur (<! liburu-kan))))
        
    (go-loop [b (<! iruzkin-kan)]
      (when b
        (iruzkin-kud b)
        (recur (<! iruzkin-kan))))

    (go (<! (azken-liburuak-lortu))
        (reset! bidea [:index nil]))
    (azken-iruzkinak-lortu))

  (fw/watch-and-reload :jsload-callback reagent/force-update-all))
