(ns nord.import
  (:require [clojure.data.csv :as csv])
  (:require [clojure.java.io :as io])
  (:require [clj-http.client :as http])
  (:require [clojure.string :as string]))

(defonce xx (csv/read-csv (io/reader "/home/eric/parks.csv")))

(def header (first xx))
(def data (rest xx))

(defn slug [s]
  (string/lower-case (string/replace (string/trim (string/replace s #"\W+" " ")) #"\s+" "_")))

(doseq [d data]
  (let [dd (into {}
                 (for [[k v] (map vector header d)
                       :when (pos? (.length v))]
                   [k v]))
        dd (assoc dd "park-id" (slug (dd "name")))]
    (prn dd)
    (http/post "http://localhost:9000/location/new"
               {:form-params
                dd})))