(ns magnet.core
  (:require [ajax.core :refer [GET POST PUT DELETE]]
            [magnet.config :refer [azken-iruzkin-kopurua]]))

(def aurriz "http://localhost:3000/v1/")

(defonce saioa (atom {:erabiltzailea nil
                      :izena nil
                      :token nil
                      :iraungitze_data nil}))
(defonce azken-iruzkinak (atom []))

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
                          :erabiltzailea era
                          :token (:token %)
                          :iraungitze_data (:iraungitze_data %))
         :error-handler #(println %)}))
#_(saioa-hasi "era" "1234")

(defn saioa-amaitu
  "Saioa amaitzen du."
  []
  (DELETE (str aurriz "saioak/" (:token @saioa))
          {:handler #(reset! saioa {:erabiltzailea nil
                                    :izena nil
                                    :token nil
                                    :iraungitze_data nil})
           :error-handler #(println %)}))
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

(GET (str aurriz "erabiltzaileak")
     {:handler #(println %)
      :error-handler #(println %)})
