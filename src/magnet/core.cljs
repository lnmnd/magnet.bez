(ns magnet.core
  (:require [ajax.core :refer [GET POST PUT DELETE]]))

(def aurriz "http://localhost:3000/v1/")
(defonce saioa (atom {:erabiltzailea nil
                      :token nil}))

(GET (str aurriz "erabiltzaileak")
     {:handler #(println %)
      :error-handler #(println %)})

(POST (str aurriz "erabiltzaileak")
     {:params {:erabiltzailea "era"
               :pasahitza "1234"
               :izena "era"}
      :format :json
      :handler #(println %)
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
