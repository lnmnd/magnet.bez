(ns magnet.core
  (:require-macros [cljs.core.async.macros :refer [go go-loop]])
  (:require [cljs.core.async :refer [chan put! <! >! timeout]]
            [ajax.core :refer [GET POST PUT DELETE]]
            [reagent.core :as reagent :refer [atom]]
            [magnet.config :refer [azken-iruzkin-kopurua azken-liburu-kopurua]]
            [magnet.bistak :as bistak]))

(enable-console-print!)

(def aurriz "http://localhost:3000/v1/")

(defonce saioa (atom {:hasita false
                      :erabiltzailea nil
                      :izena nil
                      :token nil
                      :iraungitze_data nil}))
(defonce azken-iruzkinak (atom []))
(defonce azken-liburuak (atom []))

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
           :handler #(reset! azken-iruzkinak (reverse (:iruzkinak %)))})))
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

(defn egile-guztiak-lortu
  "Egile guztien zerrenda lortzen du"
  []
  (let [kan (chan)]
    (GET (str aurriz "egileak?muga=0")
         {:response-format :json
          :keywords? true
          :handler #(put! kan (:egileak %))})
    kan))
#_(go (println (<! (egile-guztiak-lortu))))

(defn saio-kud [[mota bal]]
  "Saioaren gertaerekin zer egin erabakitzen du"
  (case mota
    :saioa-hasi (saioa-hasi (:era bal) (:pas bal))
    :saioa-amaitu (saioa-amaitu)
    nil))

(defn errendatu [saio-kon]
  (reagent/render-component [bistak/main saio-kon saioa azken-iruzkinak]
                            (.querySelector js/document "#app")))

(defn ^:export run []
  (let [saio-kon (chan)]
    
    (errendatu saio-kon)
    
    (go-loop [b (<! saio-kon)]
      (when b
        (saio-kud b)
        (recur (<! saio-kon))))

    (azken-iruzkinak-lortu)))
