(ns nord.core
  (:require
   [ring.adapter.jetty               :as jetty]

   [ring.middleware.params           :as ps]
   [ring.middleware.keyword-params   :as kw]
   [ring.middleware.json-params      :as js]
   [ring.middleware.file             :as file]
   [ring.middleware.resource         :as resource]
   [ring.middleware.file-info        :as fileinfo]
   [ring.middleware.multipart-params :as mp]
   [ring.middleware.reload           :as rel]
   [ring.middleware.session          :as sess]

   [playnice.middleware.fakemethods  :as fm]
   [playnice.middleware.weboutput    :as wo])

  (:require
   [nord.handlers                    :as handlers]))

(def port (Integer/parseInt (or (System/getenv "PORT") "9000")))
(def debug (System/getenv "DEBUG"))

(def db (or (System/getenv "DATABASE_URL")
            "jdbc:postgresql://localhost/nolaparks"))

;; put any per-request database initalization here
(defn db-middleware [hdlr]
  (fn [req]
    (hdlr (assoc req :db db))))

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
   (fileinfo/wrap-file-info
    (resource/wrap-resource
     (sess/wrap-session
      (ps/wrap-params
       (js/wrap-json-params
        (mp/wrap-multipart-params
         (kw/wrap-keyword-params
          (fm/wrap-fake-methods
           (print-req-res
            handlers/handler)))))))
     "static"))))

(defn -main [port]
  (jetty/run-jetty app {:port (Integer. port) :join false}))

(defn -dev-main [port]
  (jetty/run-jetty (rel/wrap-reload #'app) {:port (Integer. port) :join false}))
