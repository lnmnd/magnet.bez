(ns magnet.core
  (:require [ajax.core :refer [GET POST PUT DELETE]]))

(def aurriz "http://localhost:3000/v1/")

(GET (str aurriz "erabiltzaileak")
     {:handler #(println %)
      :error-handler #(println %)})
