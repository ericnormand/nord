(ns nord.import
  (:require [clojure.data.csv :as csv])
  (:require [clojure.java.io :as io])
  (:require [clojure.string :as string])
  (:require [nord.parks :as parks])
  (:require [nord.attributes :as attrs])
  (:require [clojure.walk :as walk]))

(defonce xx (csv/read-csv (io/reader "/home/eric/Downloads/nordc-import1.csv")))

(def header (first xx))
(def data (rest xx))

(defn slug [s]
  (string/lower-case (string/replace (string/trim (string/replace s #"\W+" " ")) #"\s+" "-")))

(defn bool [x]
  (prn x)
  (pos? (.length (.trim x))))
(defn string [x] x)
(defn number [x]
  (if (pos? (.length x))
    (Double/parseDouble x)
    0))

(def attr-types
  {"super-saturday" "bool"
   "name" "string"
   "address" "string"
   "acreage" "number"
   "active-passive" "string"
   "booster-club" "bool"
   "recreation-center" "bool"
   "club-house" "bool"
   "indoor-pool" "bool"
   "outdoor-pool" "bool"
   "restroom-building" "bool"
   "port-o-lets" "bool"
   "water-fountain" "bool"
   "showers-pool" "bool"
   "showers-recreation-center" "bool"
   "high-mast-lighting" "bool"
   "batting-cage" "bool"
   "bleachers" "bool"
   "dug-outs" "bool"
   "playground" "bool"
   "fencing" "bool"
   "facility-lights" "bool"
   "stadium" "bool"
   "track-field" "bool"
   "tennis-courts" "number"
   "tennis-building" "number"
   "indoor-basketball-courts" "number"
   "outdoor-basketball-courts" "number"
   "outdoor-covered-basketball-courts" "number" 
   "all-purpose-field" "bool"
   "hours-of-operation" "string"
   "off-street-parking" "bool"})

(def type-transform
  {"checkbox" bool
   "number" number
   "text" string
   "choices" string})

(defn run-re-store-attrs [amzn]
  (doseq [attr (attrs/all amzn)]
    (when (= "number" (attr-types (:attribute-id attr)))
      (attrs/store amzn (assoc attr :type "number")))))

(defn run-import [amzn]
  (let [attrs (attrs/all amzn)
        attrs (zipmap (map :attribute-id attrs) attrs)]
    (doseq [d data]
      (prn d)
     (let [dd (into {}
                    (for [[k v] (zipmap header d)
                          :let [x ((type-transform (:type (attrs k))) v)]]
                      [k x]))
           dd (walk/keywordize-keys (assoc dd "park-id" (slug (dd "name"))))]
       (prn dd)
       (parks/store amzn dd)))))