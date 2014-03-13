(ns nord.attributes
  (:require
   [clojure.java.jdbc :as db])
  (:require
   [nord.json :as json]))

(def attr-table "NOLAParks-attributes")

(defn fetch [db id]
  (json/parse
   (db/query
    db
    ["SELECT attribute
      FROM attributes
      WHERE id = ?"
     id]
    :row-fn :attribute
    :result-set-fn first)))

(defn store [db attr]
  (db/with-db-transaction [db db]
    (let [r (db/execute!
             db
             ["UPDATE attributes
               SET attribute = ?
               WHERE id = ?"
              (json/gen attr)
              (:attribute-id attr)])]
      (if (= [1] r)
        true
        (= [1]
           (db/execute!
            db
            ["INSERT INTO attributes (id, attribute)
              SELECT ?, ?
              WHERE NOT EXISTS (SELECT 1 FROM attributes WHERE id = ?)"
             (:attribute-id attr) (json/gen attr)
             (:attribute-id attr)]))))))

(defn all [db]
  (sort-by :label
           (map json/parse
                (db/query
                 db
                 ["SELECT attribute
                   FROM attributes"]
                 :row-fn :attribute))))

(defn delete [db id]
  (= [1] (db/execute!
          db
          ["DELETE FROM attributes
            WHERE id = ?"
           id])))
