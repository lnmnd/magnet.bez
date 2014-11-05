(ns magnet.core
  (:require [ajax.core :refer [GET POST PUT DELETE]]))

(def aurriz "http://localhost:3000/v1/")

(defonce saioa (atom {:erabiltzailea nil
                      :token nil}))

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

(GET (str aurriz "erabiltzaileak")
     {:handler #(println %)
      :error-handler #(println %)})


(POST (str aurriz "saioak")
      {:params {:erabiltzailea "era"
               :pasahitza "1234"
               :izena "era"}
       :format :json
       :handler #(swap! saioa assoc
                        :erabiltzailea "era"
                        :token (% "token"))
       :error-handler #(println %)})

(DELETE (str aurriz "erabiltzaileak/" (:erabiltzailea @saioa) "?token=" (:token @saioa))
     {:handler #(println %)
      :error-handler #(println %)})
