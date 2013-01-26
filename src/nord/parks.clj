(ns nord.parks
  (:require [nord.dynamo            :as dynamo])
  (:require [nord.json              :as json]))

(def park-table "NOLAParks-parks")

(defn fetch [amzn id]
  (json/parse
   (get (dynamo/amzn-get amzn park-table id) "park")))

(defn store [amzn park]
  (dynamo/amzn-set amzn park-table
                   {:park-id (:park-id park)
                    :park (json/gen park)}))

(defn all [amzn]
  (map #(json/parse (get % "park"))
       (:items (dynamo/amzn-list amzn park-table))))