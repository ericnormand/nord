(ns nord.json
  (:require [clj-json.core :as json]))

(defn gen [o]
  (json/generate-string o))

(defn parse [s]
  (when s
    (json/parse-string s true)))