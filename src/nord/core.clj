(ns nord.core
  (:require [ring.adapter.jetty               :as jetty])
  (:require [clojure.repl                     :as repl])

  (:require [ring.middleware.params           :as ps])
  (:require [ring.middleware.keyword-params   :as kw])
  (:require [ring.middleware.json-params      :as js])
  (:require [ring.middleware.file             :as file])
  (:require [ring.middleware.resource         :as resource])
  (:require [ring.middleware.file-info        :as fileinfo])
  (:require [ring.middleware.multipart-params :as mp])
  (:require [ring.middleware.reload           :as rel])
  (:require [ring.middleware.session          :as sess])

  (:require [playnice.middleware.fakemethods  :as fm])
  (:require [playnice.middleware.weboutput    :as wo])
  (:require [clojure.java.io                  :as io])
  (:require [nord.handlers                    :as handlers])
  (:gen-class))

(def port (Integer/parseInt (or (System/getenv "PORT") "9000")))
(def debug (System/getenv "DEBUG"))
(def aws-access-key (or (System/getenv "AWS_ACCESS_KEY_ID")
                        (System/getProperty "AWS_ACCESS_KEY_ID")))
(def aws-secret-key (or (System/getenv "AWS_SECRET_KEY")
                        (System/getProperty "AWS_SECRET_KEY")))

;; put any per-request database initalization here
(defn db-middleware [hdlr]
  (fn [req]
    (hdlr (assoc req
            :amzn {:access-key aws-access-key
                   :secret-key aws-secret-key}))))

(defn print-req-res [hdlr]
  (fn [req]
    (try
      (comment (when debug
         (prn "Request headers: \n" req)))
      (let [resp (hdlr req)]
        (when debug
          (comment (println "Response headers: \n" (dissoc resp :body))))
        resp)
      (catch Throwable t
        (.printStackTrace t)
        {:headers {} :status 500 :body (str t)}))))

(defn wrap-accept-charset [hdlr]
  (fn [req]
    (if (get-in req [:headers "accept-charset"])
      (hdlr req)
      (assoc-in (hdlr (assoc-in req [:headers "accept-charset"] "utf-8")) [:headers "oops"] "hey"))))

(def app
  (db-middleware
   (let [k (fileinfo/wrap-file-info
            (resource/wrap-resource
             (sess/wrap-session 
              (ps/wrap-params
               (js/wrap-json-params
                (mp/wrap-multipart-params
                 (kw/wrap-keyword-params
                  (fm/wrap-fake-methods
                   (print-req-res
                    handlers/handler)))))))
             "static"))]
     (if debug
       (rel/wrap-reload k :dirs ["src"])
       k))))

(defn -main []
  (repl/set-break-handler! (fn [signal]
                             (println signal)
                             (System/exit 0)))
  (jetty/run-jetty #'app {:port port}))