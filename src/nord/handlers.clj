(ns nord.handlers
  (:require [liberator.core         :as liberator])
  (:require [nord.view              :as view])
  (:require [clojure.walk           :as walk])
  (:require [ring.util.codec        :as codec])
  (:require [clj-json.core          :as json])
  (:require [aws.sdk.s3             :as s3])
  (:require [playnice.dispatch      :as dispatch])
  (:require [net.cgrand.enlive-html :as enlive])
  (:require [clojure.string         :as string])
  (:require [net.cgrand.xml         :as xml])
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

(liberator/defresource homepage
  :available-media-types ["text/html"]
  :available-charsets ["utf-8"]
  :handle-ok (fn [{:keys [request]}]
               (let [l (amzn-list (:amzn request) "NORD")]
                (view/homepage {:parks (:items l)}))))

(defn cleanup [park]
  (into {} (for [[k v] park
                 :when (pos? (.length v))]
             [k v])))

(defn unload-choices [attribute]
  (if (:choices attribute)
    (update-in attribute [:choices] json/parse-string)
    attribute))

(defn attr-todb [attribute]
  (-> attribute
      (update-in [:order] (fnil str "0"))
      (cleanup)))

(defn parse-order [attribute]
  (when attribute
    (update-in attribute [:order] (fnil #(Integer/parseInt %) "0"))))

(defn attr-fromdb [attribute]
  (-> attribute
      (walk/keywordize-keys)
      (unload-choices)
      (parse-order)))

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



(liberator/defresource park
  :method-allowed? #{:get :head :post}
  :exists? (fn [{:keys [request]}]
             (let [amzn (:amzn request)
                   park-id (:park-id request)
                   park
                   (walk/keywordize-keys
                    (amzn-get amzn "NORD" park-id))]
               (prn park)
               [(not (nil? park))
                {:park park}]))
  :post! (fn [{:keys [request]}]
           (let [amzn (:amzn request)]
             (amzn-set amzn "NORD" (cleanup (:params request)))))
  :post-redirect? true
  :see-other (fn [{:keys [request]}]
               {:headers {"Location" (str "/location/" (:park-id (:params request)))}
                :status 303
                :body ""})
  :handle-not-found (fn [{:keys [request]}]
                      (view/not-found (:uri request)))
  :available-media-types ["text/html"]
  :available-charsets ["utf-8"]
  :handle-ok (fn [context]
               (let [amzn (:amzn (:request context))
                     attributes (amzn-list amzn "NORD-attributes")]
                 (view/park (sort-by :order
                                     (map attr-fromdb (:items attributes)))
                            (:park context)))))

(liberator/defresource new-park
  :method-allowed? #{:get :head :post}
  :available-media-types ["text/html"]
  :available-charsets ["utf-8"]
  :post! (fn [{:keys [request]}]
           (let [amzn (:amzn request)
                 park-id (:park-id (:params request))
                 existing (amzn-get amzn "NORD" park-id)]
             (if-not existing
               (amzn-set amzn "NORD"
                                (cleanup (:params request))))))
  :post-redirect? true
  :see-other (fn [{:keys [request]}]
               {:headers {"Location" (str "/location/" (:park-id (:params request)))}
                :status 303
                :body ""})
  :handle-ok (fn [{:keys [request]}]
               (let [amzn (:amzn request)
                     attributes (amzn-list amzn "NORD-attributes")]
                 (view/new-park (sort-by :order
                                         (map attr-fromdb (:items attributes)))
                                (:park-id request)))))

(liberator/defresource edit-park
  :exists? (fn [{:keys [request]}]
             (let [amzn (:amzn request)
                   park-id (:park-id request)
                   park
                   (walk/keywordize-keys
                    (amzn-get amzn "NORD" park-id))]
               [(not (nil? park))
                {:park park}]))
  :handle-not-found (fn [{:keys [request]}]
                      (view/not-found (:uri request)))
  :available-media-types ["text/html"]
  :available-charsets ["utf-8"]
  :handle-ok (fn [context]
               (let [amzn (:amzn (:request context))
                     attributes (amzn-list amzn "NORD-attributes")]
                 (view/edit-park (sort-by :order
                                          (map attr-fromdb (:items attributes)))
                                 (:park context)))))

(liberator/defresource list-parks
  :available-media-types ["text/html" "application/json"]
  :available-charsets ["utf-8"]
  :handle-ok (fn [context]
               (let [amzn (:amzn (:request context))
                     parks (amzn-list amzn "NORD")]
                 (if (= "application/json" (:media-type (:representation context)))
                   (json/generate-string {:parks (:items parks)})
                   (view/list-parks (sort-by :name (map walk/keywordize-keys (:items parks))))))))

(liberator/defresource list-parks-json
  :available-media-types ["application/json"]
  :available-charsets ["utf-8"]
  :handle-ok (fn [context]
               (let [amzn (:amzn (:request context))
                     parks (amzn-list amzn "NORD")]
                 (json/generate-string {:parks (sort-by #(get % "name") (:items parks))}))))

(defn tolist [choices]
  (if (string? choices)
    [choices]
    choices))

(defn loadchoices [attribute params]
  (prn attribute)
  (if (= (:type attribute) "choices")
    (assoc attribute :choices (json/generate-string (tolist (:choice params))))
    attribute))



(liberator/defresource new-attribute
  :method-allowed? #{:get :head :post}
  :available-media-types ["text/html"]
  :available-charsets ["utf-8"]
  :post! (fn [{:keys [request]}]
           (let [amzn (:amzn request)
                 existing (attr-fromdb
                           (amzn-get amzn "NORD-attributes"
                                            (:attribute-id (:params request))))]
             (attr-todb
                                 (-> {:attribute-id (:attribute-id (:params request))
                                      :label (:label (:params request))
                                      :type (:type (:params request))
                                      :order (:order (:params request))}
                                     (loadchoices (:params request))))
             (if existing
               (prn "not saving")
               (amzn-set amzn "NORD-attributes"
                                (attr-todb
                                 (-> {:attribute-id (:attribute-id (:params request))
                                      :label (:label (:params request))
                                      :type (:type (:params request))
                                      :order (:order (:params request))}
                                     (loadchoices (:params request))))))))
  :post-redirect? true
  :see-other (fn [{:keys [request]}]
               {:headers {"Location" (str "/attribute/" (:attribute-id (:params request)))}
                :status 303
                :body ""})
  :handle-ok (fn [_]
               (view/new-attribute {})))

(liberator/defresource edit-attribute
  :exists? (fn [{:keys [request]}]
             (let [amzn (:amzn request)
                   attribute-id (:attribute-id request)
                   attribute
                   (attr-fromdb
                    (amzn-get amzn "NORD-attributes" attribute-id))]
               [(not (nil? attribute))
                {:attribute attribute}]))
  :handle-not-found (fn [{:keys [request]}]
                      (view/not-found (:uri request)))
  :available-media-types ["text/html"]
  :available-charsets ["utf-8"]
  :handle-ok (fn [context]
               (view/edit-attribute (:attribute context))))

(liberator/defresource attribute
  :method-allowed? #{:get :head :put :delete}
  :exists? (fn [{:keys [request]}]
             (let [amzn (:amzn request)
                   attribute-id (:attribute-id request)
                   attribute
                   (attr-fromdb
                    (amzn-get amzn "NORD-attributes" attribute-id))]
               [(not (nil? attribute))
                {:attribute attribute}]))
  :put! (fn [{:keys [request]}]
          (let [amzn (:amzn request)]
            (amzn-set amzn "NORD-attributes"
                             (attr-todb
                              (-> {:attribute-id (:attribute-id (:params request))
                                   :label (:label (:params request))
                                   :type (:type (:params request))
                                   :order (:order (:params request))}
                                  (loadchoices (:params request)))))))
  :new? (fn [{:keys [attribute]}]
          (nil? attribute))
  :handle-created (fn [{:keys [request]}]
                    {:headers {"location" (str "/attribute/" (:attribute-id (:params request)))}
                     :status 201})
  :delete! (fn [{:keys [request]}]
             (let [amzn (:amzn request)
                   attribute-id (:attribute-id request)]
               (rotary/delete-item amzn "NORD-attributes" attribute-id)))
  :handle-not-found (fn [{:keys [request]}]
                      (view/not-found (:uri request)))
  :available-media-types ["text/html"]
  :available-charsets ["utf-8"]
  :handle-ok (fn [context]
               (view/attribute (:attribute context))))

(liberator/defresource list-attributes
  :available-media-types ["text/html"]
  :available-charsets ["utf-8"]

  :handle-ok (fn [context]
               (let [amzn (:amzn (:request context))
                     attributes (amzn-list amzn "NORD-attributes")]
                 (view/list-attributes (sort-by :order
                                                (map attr-fromdb (:items attributes)))))))

(def dp (-> nil
            (dispatch/dassoc "/attribute/new" new-attribute)
            (dispatch/dassoc "/attribute/:attribute-id/edit" edit-attribute)
            (dispatch/dassoc "/attribute/:attribute-id" attribute)
            (dispatch/dassoc "/attribute/list" list-attributes)
            
            (dispatch/dassoc "/location/new" new-park)
            (dispatch/dassoc "/location/:park-id/edit" edit-park)
            (dispatch/dassoc "/location/:park-id" park)
            (dispatch/dassoc "/location/list" list-parks)
            (dispatch/dassoc "/location/list.json" list-parks-json)
            
            
            (dispatch/dassoc "/" homepage)))

(defn handler [req]
  (dispatch/dispatch dp req))