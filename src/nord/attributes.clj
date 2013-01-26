(ns nord.attributes
  (:require [nord.dynamo            :as dynamo])
  (:require [nord.json              :as json]))

(def attr-table "NOLAParks-attributes")

(defn fetch [amzn id]
  (json/parse
   (get (dynamo/amzn-get amzn attr-table id) "attribute")))

(defn store [amzn attr]
  (dynamo/amzn-set amzn attr-table
                   {:attribute-id (get attr :attribute-id)
                    :attribute (json/gen attr)}))

(defn all [amzn]
  (sort-by :order
           (map #(json/parse (get % "attribute"))
                (:items (dynamo/amzn-list amzn attr-table)))))

(defn delete [amzn id]
  (dynamo/amzn-delete amzn attr-table id))
