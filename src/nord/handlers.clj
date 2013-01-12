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

(liberator/defresource homepage
  :available-media-types ["text/html"]
  :available-charsets ["utf-8"]
  :handle-ok (fn [{:keys [request]}]
               (view/homepage {})))

(defn cleanup [park]
  (into {} (for [[k v] park
                 :when (pos? (.length v))]
             [k v])))

(defn unload-choices [attribute]
  (if (:choices attribute)
    (update-in attribute [:choices] json/parse-string)
    attribute))

(liberator/defresource park
  :method-allowed? #{:get :head :post}
  :exists? (fn [{:keys [request]}]
             (let [amzn (:amzn request)
                   park-id (:park-id request)
                   park
                   (walk/keywordize-keys
                    (rotary/get-item amzn "NORD" park-id))]
               (prn park)
               [(not (nil? park))
                {:park park}]))
  :post! (fn [{:keys [request]}]
           (let [amzn (:amzn request)]
             (prn "hello")
             (prn (cleanup (:params request)))
             (rotary/put-item amzn "NORD" (cleanup (:params request)))))
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
                     attributes (rotary/scan amzn "NORD-attributes")]
                 (view/park (map (comp unload-choices walk/keywordize-keys) (:items attributes))
                            (:park context)))))

(liberator/defresource new-park
  :method-allowed? #{:get :head :post}
  :available-media-types ["text/html"]
  :available-charsets ["utf-8"]
  :post! (fn [{:keys [request]}]
           (let [amzn (:amzn request)
                 park-id (:park-id request)
                 existing (rotary/get-item amzn "NORD" park-id)]
             (if-not existing
               (rotary/put-item amzn "NORD"
                                (cleanup (:params request))))))
  :post-redirect? true
  :see-other (fn [{:keys [request]}]
               {:headers {"Location" (str "/location/" (:park-id (:params request)))}
                :status 303
                :body ""})
  :handle-ok (fn [{:keys [request]}]
               (let [amzn (:amzn request)
                     attributes (rotary/scan amzn "NORD-attributes")]
                 (view/new-park (map (comp unload-choices walk/keywordize-keys) (:items attributes))
                                (:park-id request)))))

(liberator/defresource edit-park
  :exists? (fn [{:keys [request]}]
             (let [amzn (:amzn request)
                   park-id (:park-id request)
                   park
                   (walk/keywordize-keys
                    (rotary/get-item amzn "NORD" park-id))]
               [(not (nil? park))
                {:park park}]))
  :handle-not-found (fn [{:keys [request]}]
                      (view/not-found (:uri request)))
  :available-media-types ["text/html"]
  :available-charsets ["utf-8"]
  :handle-ok (fn [context]
               (let [amzn (:amzn (:request context))
                     attributes (rotary/scan amzn "NORD-attributes")]
                 (view/edit-park (map (comp unload-choices walk/keywordize-keys) (:items attributes))
                                 (:park context)))))

(liberator/defresource list-parks
  :available-media-types ["text/html"]
  :available-charsets ["utf-8"]
  :handle-ok (fn [context]
               (let [amzn (:amzn (:request context))
                     parks (rotary/scan amzn "NORD")]
                 (view/list-parks (map walk/keywordize-keys (:items parks)))
                 ))
  )

(defn loadchoices [attribute params]
  (prn attribute)
  (if (= (:type attribute) "choices")
    (assoc attribute :choices (json/generate-string (:choice params)))
    attribute))



(liberator/defresource new-attribute
  :method-allowed? #{:get :head :post}
  :available-media-types ["text/html"]
  :available-charsets ["utf-8"]
  :post! (fn [{:keys [request]}]
           (let [amzn (:amzn request)
                 existing (unload-choices
                           (walk/keywordize-keys
                            (rotary/get-item amzn "NORD-attributes"
                                             (:attribute-id (:params request)))))]
             (if existing
               nil
               (rotary/put-item amzn "NORD-attributes"
                                (-> {:attribute-id (:attribute-id (:params request))
                                     :label (:label (:params request))
                                     :type (:type (:params request))}
                                    (loadchoices (:params request)))))))
  :post-redirect? true
  :see-other (fn [{:keys [request]}]
               {:headers {"Location" (str "/attribute/" (:attribute-id (:params request)))}
                :status 303
                :body ""})
  :handle-ok (fn [{:keys [request]}]
               (view/new-attribute {})))

(liberator/defresource edit-attribute
  :exists? (fn [{:keys [request]}]
             (let [amzn (:amzn request)
                   attribute-id (:attribute-id request)
                   attribute
                   (unload-choices
                    (walk/keywordize-keys
                     (rotary/get-item amzn "NORD-attributes" attribute-id)))]
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
                   (unload-choices
                    (walk/keywordize-keys
                     (rotary/get-item amzn "NORD-attributes" attribute-id)))]
               [(not (nil? attribute))
                {:attribute attribute}]))
  :put! (fn [{:keys [request]}]
          (let [amzn (:amzn request)]
            (rotary/put-item amzn "NORD-attributes"
                             (-> {:attribute-id (:attribute-id (:params request))
                                  :label (:label (:params request))
                                  :type (:type (:params request))}
                                 (loadchoices (:params request))))))
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
                     attributes (rotary/scan amzn "NORD-attributes")]
                 (view/list-attributes (map (comp unload-choices walk/keywordize-keys) (:items attributes))))))

(def dp (-> nil
            (dispatch/dassoc "/attribute/new" new-attribute)
            (dispatch/dassoc "/attribute/:attribute-id/edit" edit-attribute)
            (dispatch/dassoc "/attribute/:attribute-id" attribute)
            (dispatch/dassoc "/attribute/list" list-attributes)
            (dispatch/dassoc "/location/new" new-park)
            (dispatch/dassoc "/location/:park-id/edit" edit-park)
            (dispatch/dassoc "/location/:park-id" park)
            (dispatch/dassoc "/location/list" list-parks)
            (dispatch/dassoc "/" homepage)))

(defn handler [req]
  (dispatch/dispatch dp req))