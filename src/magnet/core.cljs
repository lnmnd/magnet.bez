(ns magnet.core
  (:require [ajax.core :refer [GET POST PUT DELETE]]))

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
         :handler #(swap! saioa assoc
                          :erabiltzailea era
                          :token (% "token")
                          :iraungitze_data (% "iraungitze_data"))
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
  "TODO oraingoz iruzkin guztiak lortzen ditu."
  []
  (GET (str aurriz "iruzkinak")
       {:response-format :json
        :keywords? true
        :handler #(reset! azken-iruzkinak (:iruzkinak %))
        :error-handler #(println %)}))
#_(azken-iruzkinak-lortu)

(GET (str aurriz "erabiltzaileak")
     {:handler #(println %)
      :error-handler #(println %)})
