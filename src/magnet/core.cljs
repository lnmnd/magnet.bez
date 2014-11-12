(ns magnet.core
  (:require-macros [cljs.core.async.macros :refer [go go-loop]])
  (:require [cljs.core.async :as async :refer [chan put! <! >! timeout]]
            [ajax.core :refer [GET POST PUT DELETE]]
            [reagent.core :as reagent :refer [atom]]
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
               :token nil
               :iraungitze_data nil}))
(defonce ^{:doc "Uneko bidea"}
  bidea (atom []))
(defonce ^{:doc "Azken iruzkinen zerrenda"}
  azken-iruzkinak (atom []))
(defonce ^{:doc "Azken liburuen zerrenda"}
  azken-liburuak (atom []))
(defonce ^{:doc "Liburuaren datuak"}
  liburua (atom {}))

(defn entzun
  "Helbidea entzuten du eta erantzunari funtzioa aplikatzen dio.
   Emaitza duen kanala itzultzen du"
  [helbidea f]
  (let [kan (chan)]
    (GET helbidea
         {:response-format :json
          :keywords? true
          :handler #(put! kan (f %))})
    kan))

(defn erabiltzailea-gehitu
  "Erabiltzaile berri bat gehitzen du."
  ([era pas izen]
     (erabiltzailea-gehitu era pas izen ""))
  ([era pas izen desk]
     (let [param {:erabiltzailea era
                  :pasahitza pas
                  :izena izen}]
       (POST (str aurriz "erabiltzaileak")
             {:params (if (empty? desk) param (assoc param :deskribapena desk)) 
              :format :json
              :handler #(println %)
              :error-handler #(println %)}))))
#_(erabiltzailea-gehitu "era" "1234" "era")

(defn saioa-hasi
  "Saioa hasten du."
  [era pas]
  (POST (str aurriz "saioak")
        {:params {:erabiltzailea era
                  :pasahitza pas}
         :format :json
         :response-format :json
         :keywords? true
         :handler #(swap! saioa assoc
                          :hasita true
                          :erabiltzailea era
                          :token (:token %)
                          :iraungitze_data (:iraungitze_data %))
         :error-handler #(println %)}))
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

(defn iruzkin-liburu-titulua
  [ir]
  (entzun (str aurriz "liburuak/" (:liburua ir))
          (fn [x]
            (assoc ir :liburu_titulua (:titulua (:liburua x))))))

(defn azken-iruzkinak-lortu
  "Azken iruzkinak lortzen ditu"
  ([]
     (GET (str aurriz "iruzkinak")
          {:response-format :json
           :keywords? true
           :handler #(azken-iruzkinak-lortu (if (> (:guztira %) azken-iruzkin-kopurua)
                                              (- (:guztira %) azken-iruzkin-kopurua)
                                              0))}))
  ([desp]
     (GET (str aurriz "iruzkinak?desplazamendua=" desp "&muga=" azken-iruzkin-kopurua)
          {:response-format :json
           :keywords? true
           :handler #(do (go (println (<! (iruzkin-liburu-titulua {:liburua 1}))))
                         (reset! azken-iruzkinak (reverse (:iruzkinak %))))})))
#_(azken-iruzkinak-lortu)

(defn azken-liburuak-lortu
  "Azken liburuak lortzen ditu"
  ([]
     (GET (str aurriz "liburuak")
          {:response-format :json
           :keywords? true
           :handler #(azken-liburuak-lortu (if (> (:guztira %) azken-liburu-kopurua)
                                              (- (:guztira %) azken-liburu-kopurua)
                                              0))}))
  ([desp]
     (GET (str aurriz "liburuak?desplazamendua=" desp "&muga=" azken-liburu-kopurua)
          {:response-format :json
           :keywords? true
           :handler #(reset! azken-liburuak (reverse (:liburuak %)))})))
#_(azken-liburuak-lortu)

(defn liburua-lortu
  "id duen liburua lortzen du."
  [id]
  (GET (str aurriz "liburuak/" id)
       {:response-format :json
        :keywords? true
        :handler #(reset! liburua (:liburua %))}))
#_(liburua-lortu 1)

(defn egile-guztiak-lortu
  "Egile guztien zerrenda lortzen du"
  []
  (entzun (str aurriz "egileak?muga=0") :egileak))
#_(go (println (<! (egile-guztiak-lortu))))

(defn saio-kud [[mota bal]]
  "Saioaren gertaerekin zer egin erabakitzen du"
  (case mota
    :saioa-hasi (saioa-hasi (:era bal) (:pas bal))
    :saioa-amaitu (saioa-amaitu)
    nil))

(defn bide-kud [[mota bal]]
  "Bidearen gertaerekin zer egin erabakitzen du."
  (if (= :index mota)
    (azken-liburuak-lortu))
  (if (= :liburua mota)
    (liburua-lortu bal))
  (when (contains? #{:index :liburua-gehitu :nire-liburuak :liburua}
                   mota)
    (reset! bidea [mota bal])))

(defn errendatu [saio-kan]
  (reagent/render-component [bistak/main saio-kan saioa bidea azken-iruzkinak azken-liburuak liburua]
                            (.querySelector js/document "#app")))

(defn ^:export run []
  (let [saio-kan (chan)
        bide-kan (chan)]
    
    (errendatu saio-kan)
        
    (go-loop [b (<! saio-kan)]
      (when b
        (saio-kud b)
        (recur (<! saio-kan))))

    (go-loop [b (<! bideak/kan)]
      (when b
        (bide-kud b)
        (recur (<! bideak/kan))))

    (put! bideak/kan [:index nil])
    (azken-iruzkinak-lortu)))
