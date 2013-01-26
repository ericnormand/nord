(ns nord.dynamo
  "Wrappers around rotary client for dealing with Amazon errors.
   Basically, we retry three times."
  (:require [rotary.client          :as rotary]))

(defn amzn-list
  ([amzn db]
     (amzn-list amzn db 2))
  ([amzn db n]
     (if (pos? n)
       (try
         (rotary/scan amzn db)
         (catch Exception e
           (.printStackTrace e)
           (amzn-list amzn db (dec n))))
       (rotary/scan amzn db))))

(defn amzn-get
  ([amzn db id]
     (amzn-get amzn db id 2))
  ([amzn db id n]
     (if (pos? n)
       (try
         (rotary/get-item amzn db id)
         (catch Exception e
           (.printStackTrace e)
           (amzn-get amzn db id (dec n))))
       (rotary/get-item amzn db id))))

(defn amzn-set
  ([amzn db obj]
     (amzn-set amzn db obj 2))
  ([amzn db obj n]
     (if (pos? n)
       (try
         (rotary/put-item amzn db obj)
         (catch Exception e
           (.printStackTrace e)
           (amzn-set amzn db obj (dec n))))
       (rotary/put-item amzn db obj))))

(defn amzn-delete
  ([amzn db id]
     (amzn-delete amzn db id 2))
  ([amzn db id n]
     (if (pos? n)
       (try
         (rotary/delete-item amzn db id)
         (catch Exception e
           (.printStackTrace e)
           (amzn-delete amzn db id (dec n))))
       (amzn-delete amzn db id))))
