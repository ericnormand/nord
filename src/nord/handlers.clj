(ns nord.handlers
  (:require [liberator.core         :as liberator])
  (:require [ring.util.codec        :as codec])
  (:require [clj-json.core          :as json])
  (:require [playnice.dispatch      :as dispatch])
  (:require [clojure.string         :as string])

  (:require [nord.view              :as view])
  (:require [nord.attributes        :as attr])
  (:require [nord.parks             :as parks]))

(liberator/defresource homepage
  :available-media-types ["text/html"]
  :available-charsets ["utf-8"]
  :handle-ok (fn [{:keys [request]}]
               (view/homepage {:parks (parks/all (:amzn request))})))


(defn bool [x]
  (let [x (.trim x)]
   (and (not= x "false")
        (not= x ""))))

(defn string [x] x)
(defn number [x]
  (if (pos? (.length x))
    (Double/parseDouble x)
    0))

(def type-transform
  {"checkbox" bool
   "number" number
   "text" string
   "choices" string})

(defn transform-types [attrs park]
  (let [attrs (zipmap (map :attribute-id attrs) attrs)]
    (into {}
          (for [[k v] park
                :let [x (if (= :park-id k)
                          v
                          ((type-transform (:type (attrs (name k)))) v))]]
            [k x]))))

(liberator/defresource park
  :method-allowed? #{:get :head :post}
  :exists? (fn [{:keys [request]}]
             (let [park (parks/fetch (:amzn request)
                                     (:park-id request))]
               [(not (nil? park))
                {:park park}]))
  :post! (fn [{:keys [request]}]
           (parks/store (:amzn request)
                        (transform-types (attr/all (:amzn request))
                                         (:params request))))
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
               (view/park (attr/all (:amzn (:request context)))
                          (:park context))))

(liberator/defresource new-park
  :method-allowed? #{:get :head :post}
  :available-media-types ["text/html"]
  :available-charsets ["utf-8"]
  :post! (fn [{:keys [request]}]
           (when-not (parks/fetch (:amzn request)
                                  (:park-id (:params request)))
             (parks/store (:amzn request)
                          (transform-types (attr/all (:amzn request))
                                           (:params request)))))
  :post-redirect? true
  :see-other (fn [{:keys [request]}]
               {:headers {"Location" (str "/location/" (:park-id (:params request)))}
                :status 303
                :body ""})
  :handle-ok (fn [{:keys [request]}]
               (view/new-park (attr/all (:amzn request))
                              (:park-id request))))

(liberator/defresource edit-park
  :exists? (fn [{:keys [request]}]
             (let [amzn (:amzn request)
                   park-id (:park-id request)
                   park (parks/fetch amzn park-id)]
               [(not (nil? park))
                {:park park}]))
  :handle-not-found (fn [{:keys [request]}]
                      (view/not-found (:uri request)))
  :available-media-types ["text/html"]
  :available-charsets ["utf-8"]
  :handle-ok (fn [context]
               (let [amzn (:amzn (:request context))
                     attributes (attr/all amzn)]
                 (view/edit-park attributes
                                 (:park context)))))

(liberator/defresource list-parks
  :available-media-types ["text/html" "application/json"]
  :available-charsets ["utf-8"]
  :handle-ok (fn [context]
               (let [amzn (:amzn (:request context))
                     parks (parks/all amzn)]
                 (if (= "application/json" (:media-type (:representation context)))
                   (json/generate-string {:parks parks})
                   (view/list-parks (sort-by :name parks))))))

(liberator/defresource list-parks-json
  :available-media-types ["application/json"]
  :available-charsets ["utf-8"]
  :handle-ok (fn [context]
               (let [amzn (:amzn (:request context))
                     parks (parks/all amzn)]
                 (json/generate-string {:parks parks}))))

(defn tolist [choices]
  (if (string? choices)
    [choices]
    choices))

(defn loadchoices [attribute params]
  (if (= (:type attribute) "choices")
    (assoc attribute :choices (tolist (:choice params)))
    attribute))

(defn parse-int [s]
  (try
    (Double/parseDouble s)
    (catch Exception e
      nil)))

(liberator/defresource new-attribute
  :method-allowed? #{:get :head :post}
  :available-media-types ["text/html"]
  :available-charsets ["utf-8"]
  :post! (fn [{:keys [request]}]
           (let [amzn (:amzn request)
                 existing (attr/fetch amzn
                                      (:attribute-id (:params request)))]
             (when-not existing
               (attr/store amzn 
                           (loadchoices
                            {:attribute-id (:attribute-id (:params request))
                             :label (:label (:params request))
                             :type (:type (:params request))
                             :order (parse-int (:order (:params request)))}
                            (:params request))))))
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
                   attribute (attr/fetch amzn attribute-id)]
               [(not (nil? attribute))
                {:attribute attribute}]))
  :handle-not-found (fn [{:keys [request]}]
                      (view/not-found (:uri request)))
  :available-media-types ["text/html"]
  :available-charsets ["utf-8"]
  :handle-ok (fn [context]
               (view/edit-attribute (:attribute context))))

(liberator/defresource attribute
  :method-allowed? #{:get :head :post :delete}
  :exists? (fn [{:keys [request]}]
             (let [amzn (:amzn request)
                   attribute-id (:attribute-id request)
                   attribute (attr/fetch amzn attribute-id)]
               [(not (nil? attribute))
                {:attribute attribute}]))
  :post! (fn [{:keys [request]}]
           (attr/store (:amzn request)
                       (loadchoices
                        {:attribute-id (:attribute-id (:params request))
                         :label (:label (:params request))
                         :type (:type (:params request))
                         :order (parse-int (:order (:params request)))}
                        (:params request))))
  :post-redirect? true
  :see-other (fn [{:keys [request]}]
               {:headers {"Location" (str "/attribute/" (:attribute-id (:params request)))}
                :status 303
                :body ""})
  :delete! (fn [{:keys [request]}]
             (attr/delete (:amzn request) (:attribute-id request)))
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
               (view/list-attributes (attr/all (:amzn (:request context))))))

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