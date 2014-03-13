(ns nord.parks
  (:require
   [clojure.java.jdbc :as db])
  (:require
   [nord.json :as json]))

(defn fetch [db id]
  (json/parse
   (db/query
    db
    ["SELECT location
      FROM locations
      WHERE id = ?"
     id]
    :row-fn :location
    :result-set-fn first)))

(defn store [db location]
  (db/with-db-transaction [db db]
    (let [r (db/execute!
             db
             ["UPDATE locations
             SET location = ?
             WHERE id = ?"
              (:park-id location)
              (json/gen location)])]
      (if (= [1] r)
        true
        (= [1]
           (db/execute!
            db
            ["INSERT INTO locations (id, location)
            SELECT ?, ?
            WHERE NOT EXISTS (SELECT 1 FROM locations WHERE id = ?)"
             (:park-id location) (json/gen location)
             (:park-id location)]))))))

(defn all [db]
  (sort-by :name
           (map json/parse
                (db/query
                 db
                 ["SELECT location
                   FROM locations"]
                 :row-fn :location))))
