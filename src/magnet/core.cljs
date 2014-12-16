(ns magnet.core
  (:require-macros [cljs.core.async.macros :refer [go go-loop]])
  (:require [cljs.core.async :as async :refer [chan put! <! >! timeout]]
            [ajax.core :refer [GET POST PUT DELETE]]
            [cljs-time.format :as format]
            [cljs-time.coerce :as coerce]            
            [reagent.core :as reagent :refer [atom]]
            [figwheel.client :as fw]
            [magnet.bideak :as bideak]
            [magnet.bistak.core :as bistak]))

(enable-console-print!)

(defonce ^{:doc "APIaren aurrizkia"}
  aurriz "")
(defonce azken-gogoko-kopurua 0)
(defonce azken-iruzkin-kopurua 0)
(defonce liburu-orriko 0)

(defonce ^{:doc "Erabiltzailearen saioa"}
  saioa (atom {:hasiera-okerra false
               :hasita false
               :erabiltzailea nil
               :izena nil
               :deskribapena nil
               :token nil
               :iraungitze_data nil}))
(defonce ^{:doc "Uneko bidea"}
  bidea (atom []))
(defonce ^{:doc "Azken liburuetatik gogokoena"}
  azken-gogokoena (atom {}))
(defonce ^{:doc "Azken iruzkinen zerrenda"}
  azken-iruzkinak (atom []))
(defonce ^{:doc "Nire liburuen zerrenda"}
  nire-liburuak (atom []))
(defonce ^{:doc "Nik idatzitako iruzkinen zerrenda"}
  nire-iruzkinak (atom []))
(defonce ^{:doc "Nire gogoko liburuen zerenda"}
  nire-gogokoak (atom []))
(defonce ^{:doc "Guztira duden liburu kopurua"}
  liburu-kopurua (atom 0))
(defonce ^{:doc "Orriak: uneko orria eta orri kopurua"}
  orriak (atom [1 1]))
(defonce ^{:doc "Liburuen zerrenda"}
  liburuak (atom []))
(defonce ^{:doc "Liburuaren datuak"}
  liburua (atom {}))
(defonce ^{:doc "Uneko liburuaren iruzkinak"}
  liburuaren-iruzkinak (atom {}))
(defonce ^{:doc "Erabiltzaileen zerrenda"}
  erabiltzaileak (atom []))
(defonce ^{:doc "Tituluen zerrenda"}
  tituluak (atom []))
(defonce ^{:doc "Egileen zerrenda"}
  egileak (atom []))
(defonce ^{:doc "Argitaletxeen zerrenda"}
  argitaletxeak (atom []))
(defonce ^{:doc "Generoen zerrenda"}
  generoak (atom []))
(defonce ^{:doc "Etiketen zerrenda"}
  etiketak (atom []))

(defn entzun
  "Helbidea entzuten du eta erantzunari funtzioa aplikatzen dio.
  Emaitza duen kanala itzultzen du.

  Adb:
  (go (let [ema (<! (entzun \"helbidea\"))]
        (println ema)))"
  ([helbidea]
   (entzun helbidea identity))  
  ([helbidea f]
   (let [kan (chan)]
     (GET helbidea
          {:response-format :json
           :keywords? true
           :handler #(put! kan (f %))})
     kan))
  ([helbidea f ef]
   (let [kan (chan)]
     (GET helbidea
          {:response-format :json
           :keywords? true
           :handler #(put! kan (f %))
           :error-handler #(put! kan (ef %))})
     kan)))

(defn bidali-eta-entzun
  "Helbidera datuak bidaltzen ditu eta erantzunari funtzioa aplikatzen dio.
  Emaitza duen kanala itzultzen du"
  ([helbidea dat]
   (bidali-eta-entzun helbidea dat identity))  
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
  ([helbidea dat]
   (aldatu-eta-entzun helbidea dat identity))  
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
                          (if (empty? desk) param (assoc param :deskribapena desk))))))

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
                          (if (empty? desk) param (assoc param :deskribapena desk))))))

(declare saioa-hasi)
(defn saioa-berritu
  "seg segundu pasa ondoren saioa berriz hasten du saioa oraindik ez bada amaitu."
  [seg]
  (go (<! (timeout (* 1000 seg)))
      (when (:hasita @saioa)
        (saioa-hasi (:erabiltzailea @saioa) (:pasahitza @saioa)))))

(defn saioa-hasi
  "Saioa hasten du."
  [era pas]
  (bidali-eta-entzun
   (str aurriz "saioak")
   {:erabiltzailea era
    :pasahitza pas}
   #(go (let [dat (<! (erabiltzailea-lortu era))
              formatua (format/formatters :date-time-no-ms)
              form->ms (fn [x] (coerce/to-long (format/parse formatua x)))]
          ; iraungitu baino 10 s lehenago saioa berritu
          (saioa-berritu (* 1000 (- (form->ms (:iraungitze_data %)) (form->ms (:saio_hasiera %)) 10)))
          (swap! saioa assoc
                 :hasiera-okerra false
                 :hasita true
                 :erabiltzailea era
                 :pasahitza pas
                 :izena (:izena dat)
                 :deskribapena (:deskribapena dat)
                 :token (:token %)
                 :iraungitze_data (:iraungitze_data %)))
        true)
   #(do (swap! saioa assoc :hasiera-okerra true)
        false)))

(defn saioa-amaitu
  "Saioa amaitzen du."
  []
  (DELETE (str aurriz "saioak/" (:token @saioa)))
  (reset! saioa {:hasita false
                 :erabiltzailea nil
                 :izena nil
                 :token nil
                 :iraungitze_data nil}))

(defn erabiltzailea-ezabatu
  "Erabiltzailea ezabatu eta saioa amaitzen du."
  []
  (DELETE (str aurriz "erabiltzaileak/" (:erabiltzailea @saioa) "?token=" (:token @saioa))
          {:handler #(saioa-amaitu)}))

(defn liburua-gehitu [edukia]
  (bidali-eta-entzun (str aurriz "liburuak?token=" (:token @saioa))
                     edukia
                     :liburua))

(defn liburua-ezabatu [id]
  (DELETE (str aurriz "liburuak/" id "?token=" (:token @saioa))))

(defn gogokoetan-sartu [id]
  (bidali-eta-entzun (str aurriz "erabiltzaileak/" (:erabiltzailea @saioa) "/gogoko_liburuak?token=" (:token @saioa))
                     {:id id}))

(defn gogokoetatik-kendu [id]
  (DELETE (str aurriz "erabiltzaileak/" (:erabiltzailea @saioa) "/gogoko_liburuak/" id "?token=" (:token @saioa))))

(defn iruzkina-ezabatu [id]
  (DELETE (str aurriz "iruzkinak/" id "?token=" (:token @saioa))))

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

(defn nire-liburuak-lortu []
  (go (let [libk (<! (entzun (str aurriz "erabiltzaileak/" (:erabiltzailea @saioa) "/liburuak")
                             :liburuak))]
        (reset! nire-liburuak (rseq libk)))))

(defn nire-iruzkinak-lortu []
  (go (let [irk (<! (entzun (str aurriz "erabiltzaileak/" (:erabiltzailea @saioa) "/iruzkinak?muga=0")
                            :iruzkinak))]
        (reset! nire-iruzkinak (rseq irk)))))

(defn nire-gogokoak-lortu []
  (go (let [xs (<! (entzun (str aurriz "erabiltzaileak/" (:erabiltzailea @saioa) "/gogoko_liburuak?muga=0")
                            :gogoko_liburuak))]
        (reset! nire-gogokoak (rseq xs)))))

(defn- gogokoena [x y]
  (if (> (:gogoko_kopurua x) (:gogoko_kopurua y))
    x
    y))

(defn azken-gogokoena-lortu
  "Azken liburuetatik gogokoena lortzen du."
  ([]
   (go (let [guztira (<! (entzun (str aurriz "liburuak") :guztira))]
         (azken-gogokoena-lortu (if (> guztira azken-gogoko-kopurua)
                                  (- guztira azken-gogoko-kopurua)
                                  0)))))
  ([desp]
   (go (let [liburuak (<! (entzun (str aurriz "liburuak?desplazamendua=" desp "&muga=" liburu-orriko) :liburuak))]
         (reset! azken-gogokoena (reduce gogokoena {:gogoko_kopurua -1} liburuak))))))

(defn liburuak-lortu
  "Liburuak lortzen ditu"
  ([]
     (entzun (str aurriz "liburuak")
             #(do (reset! liburu-kopurua (:guztira %))
                  (liburuak-lortu (if (> @liburu-kopurua liburu-orriko)
                                    (- @liburu-kopurua liburu-orriko)
                                    0)))))
  ([desp]
     (entzun (str aurriz "liburuak?desplazamendua=" desp "&muga=" liburu-orriko)
             #(reset! liburuak (reverse (:liburuak %))))))

(defn liburua-lortu
  "id duen liburua lortzen du."
  [id]
  (entzun (str aurriz "liburuak/" id)
          #(reset! liburua (:liburua %))))

(defn liburuaren-iruzkinak-lortu
  "id duen libururen iruzkinak lortzen ditu."
  [id]
  (entzun (str aurriz "liburuak/" id "/iruzkinak?muga=0")
          #(reset! liburuaren-iruzkinak (:iruzkinak %))))

(defn erabiltzaileak-lortu
  "Erabiltzaile guztiak lortzen ditu."
  []
  (entzun (str aurriz "erabiltzaileak?muga=0")
          #(reset! erabiltzaileak (map :erabiltzailea (:erabiltzaileak %)))))

(defn tituluak-lortu
  "Titulu guztiak lortzen ditu."
  []
  (entzun (str aurriz "tituluak?muga=0")
          #(reset! tituluak (:tituluak %))))

(defn egileak-lortu
  "Egile guztiak lortzen ditu."
  []
  (entzun (str aurriz "egileak?muga=0")
          #(reset! egileak (:egileak %))))

(defn argitaletxeak-lortu
  "Argitaletxe guztiak lortzen ditu."
  []
  (entzun (str aurriz "argitaletxeak?muga=0")
          #(reset! argitaletxeak (:argitaletxeak %))))

(defn generoak-lortu
  "Genero guztiak lortzen ditu."
  []
  (entzun (str aurriz "generoak?muga=0")
          #(reset! generoak (:generoak %))))

(defn etiketak-lortu
  "Etiketa guztiak lortzen ditu."
  []
  (entzun (str aurriz "etiketak?muga=0")
          #(reset! etiketak (:etiketak %))))

(defn iruzkina-gehitu
  "id liburuari erantzuten dion iruzkina gehitzen du."
  [id edukia]
  (bidali-eta-entzun
   (str aurriz "liburuak/" id "/iruzkinak?token=" (:token @saioa))
   edukia))

(defn egile-guztiak-lortu
  "Egile guztien zerrenda lortzen du"
  []
  (entzun (str aurriz "egileak?muga=0") :egileak))

(defn bide-kud [[mota bal]]
  "Bidearen gertaerekin zer egin erabakitzen du."
  (when (= :birbidali mota)
    (set! (.-location js/window) (str "#" bal)))
  (when (= :index mota)
    (liburuak-lortu))
  (when (= :liburuak mota)
    (liburuak-lortu))
  (when (= :erregistratu mota)
    (erabiltzaileak-lortu))    
  (when (= :liburua mota)
    (liburua-lortu bal)
    (liburuaren-iruzkinak-lortu bal))
  (when (= :liburua-gehitu mota)
    (tituluak-lortu)    
    (egileak-lortu)
    (argitaletxeak-lortu)
    (generoak-lortu)
    (etiketak-lortu))  
  (when (contains? #{:index :liburuak :erregistratu :saioa-hasi :liburua-gehitu :nire-liburuak :nire-iruzkinak :nire-gogokoak :profila :liburua :bilatu}
                   mota)
    (reset! bidea [mota bal])))

(defn saio-kud [[mota bal] bide-kan]
  "Saioaren gertaerekin zer egin erabakitzen du"
  (case mota
    :erregistratu (do (put! bide-kan [:birbidali "profila"])
                      (go (<! (erabiltzailea-gehitu (:erabiltzailea bal) (:pasahitza bal) (:izena bal) (:deskribapena bal)))
                          (saioa-hasi (:erabiltzailea bal) (:pasahitza bal))))
    :erabiltzailea-aldatu (erabiltzailea-aldatu (:era bal) (:pas bal) (:izen bal) (:des bal))
    :erabiltzailea-ezabatu (do (put! bide-kan [:birbidali "/"])
                               (erabiltzailea-ezabatu))
    :saioa-hasi (go (when (<! (<! (saioa-hasi (:era bal) (:pas bal))))
                      (put! bide-kan [:birbidali ""])
                      (nire-gogokoak-lortu)
                      (nire-liburuak-lortu)
                      (nire-iruzkinak-lortu)))
    :saioa-amaitu (do (put! bide-kan [:birbidali ""])
                      (saioa-amaitu)) 
    nil))

(defn liburu-kud [[mota bal] bide-kan]
  "Liburuekin lotutako kudeatzailea."
  (case mota
    :liburua-gehitu (go (let [li (<! (liburua-gehitu bal))]
                          (swap! nire-liburuak conj li)
                          (>! bide-kan [:birbidali (str "/liburuak/" (:id li))])))
    :liburua-ezabatu (do (swap! nire-liburuak (fn [lk] (remove #(= bal (:id %)) lk)))
                         (liburua-ezabatu bal))
    :gogokoetan-sartu (go (swap! nire-gogokoak conj @liburua)
                          (<! (gogokoetan-sartu bal)))
    :gogokoetatik-kendu (do (swap! nire-gogokoak (fn [xs] (remove #(= bal (:id %)) xs))) 
                            (gogokoetatik-kendu bal))
    nil))

(defn erantzuna-gehitu [irak ir]
  (-> (map (fn [x]
             (if (contains? (set (:gurasoak ir)) (:id x))
               (update-in x [:erantzunak] #(conj % (:id ir)))
               x)) irak)
      (concat [ir])))

(defn iruzkin-kud [[mota bal]]
  "Iruzkinekin lotutako kudeatzailea."
  (case mota
    :iruzkina-gehitu (go (let [{ir :iruzkina} (<! (iruzkina-gehitu (:id bal) (:edukia bal)))]
                           (swap! liburuaren-iruzkinak erantzuna-gehitu ir (:gurasoak ir))
                           (swap! nire-iruzkinak conj ir)))
    :iruzkina-ezabatu (do (swap! nire-iruzkinak (fn [lk] (remove #(= bal (:id %)) lk)))
                          (iruzkina-ezabatu bal))
    nil))

(defn errendatu [{:keys [saio-kan liburu-kan iruzkin-kan]}]
  (reagent/render-component [bistak/main {:saio-kan saio-kan
                                          :liburu-kan liburu-kan
                                          :iruzkin-kan iruzkin-kan
                                          :saioa saioa
                                          :bidea bidea
                                          :azken-gogokoena azken-gogokoena
                                          :azken-iruzkinak azken-iruzkinak
                                          :erabiltzaileak erabiltzaileak
                                          :tituluak tituluak
                                          :egileak egileak
                                          :argitaletxeak argitaletxeak
                                          :generoak generoak
                                          :etiketak etiketak
                                          :liburu-kopurua liburu-kopurua
                                          :orriak orriak
                                          :liburuak liburuak
                                          :nliburuak nire-liburuak
                                          :niruzkinak nire-iruzkinak
                                          :ngogokoak nire-gogokoak
                                          :liburua liburua
                                          :lib-irak liburuaren-iruzkinak}]
                            (.querySelector js/document "#app")))

(defn ^:export run [dev, zerbitzaria, portua, kazken-gogoko-kopurua, kazken-iruzkin-kopurua, kliburu-orriko]
  (set! aurriz (str "http://" zerbitzaria ":" portua "/v1/"))
  (set! azken-gogoko-kopurua kazken-gogoko-kopurua)
  (set! azken-iruzkin-kopurua kazken-iruzkin-kopurua)
  (set! liburu-orriko kliburu-orriko)
  
  (let [saio-kan (chan)
        bide-kan (bideak/bideak-definitu)
        liburu-kan (chan)
        iruzkin-kan (chan)]
    
    (errendatu {:saio-kan saio-kan
                :liburu-kan liburu-kan
                :iruzkin-kan iruzkin-kan})
        
    (go-loop [b (<! saio-kan)]
      (when b
        (saio-kud b bide-kan)
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

    (reset! bidea [:index nil])
    (liburuak-lortu)
    (azken-gogokoena-lortu)
    (azken-iruzkinak-lortu))

  (when dev
    (fw/watch-and-reload :jsload-callback reagent/force-update-all)))
