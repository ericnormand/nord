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
               (view/homepage {:parks (parks/all (:db request))})))

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
  :allowed-methods [:get :head :post]
  :exists? (fn [{:keys [request]}]
             (let [park (parks/fetch (:db request)
                                     (:park-id request))]
               [(boolean park)
                {:park park}]))
  :post! (fn [{:keys [request]}]
           (parks/store (:db request)
                        (transform-types (attr/all (:db request))
                                         (:params request)))
           {:location (str "/location/" (:park-id (:params request)))})
  :post-redirect? true
  :handle-not-found (fn [{:keys [request]}]
                      (view/not-found (:uri request)))
  :available-media-types ["text/html"]
  :available-charsets ["utf-8"]
  :handle-ok (fn [context]
               (view/park (attr/all (:db (:request context)))
                          (:park context))))

(liberator/defresource new-park
  :allowed-methods [:get :head :post]
  :available-media-types ["text/html"]
  :available-charsets ["utf-8"]
  :post! (fn [{:keys [request]}]
           (parks/store (:db request)
                        (transform-types (attr/all (:db request))
                                         (:params request)))
           {:location (str "/location/" (:park-id (:params request)))})
  :post-redirect? true
  :handle-ok (fn [{:keys [request]}]
               (view/new-park (attr/all (:db request))
                              (:park-id request))))

(liberator/defresource edit-park
  :exists? (fn [{:keys [request]}]
             (let [amzn (:db request)
                   park-id (:park-id request)
                   park (parks/fetch amzn park-id)]
               [(boolean park)
                {:park park}]))
  :handle-not-found (fn [{:keys [request]}]
                      (view/not-found (:uri request)))
  :available-media-types ["text/html"]
  :available-charsets ["utf-8"]
  :handle-ok (fn [context]
               (let [amzn (:db (:request context))
                     attributes (attr/all amzn)]
                 (view/edit-park attributes
                                 (:park context)))))

(liberator/defresource list-parks
  :available-media-types ["text/html" "application/json"]
  :available-charsets ["utf-8"]
  :handle-ok (fn [context]
               (let [amzn (:db (:request context))
                     parks (parks/all amzn)]
                 (if (= "application/json" (:media-type (:representation context)))
                   (json/generate-string {:parks parks})
                   (view/list-parks (sort-by :name parks))))))

(liberator/defresource list-parks-json
  :available-media-types ["application/json"]
  :available-charsets ["utf-8"]
  :handle-ok (fn [context]
               (let [amzn (:db (:request context))
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
  :allowed-methods [:get :head :post]
  :available-media-types ["text/html"]
  :available-charsets ["utf-8"]
  :post! (fn [{:keys [request]}]
           (let [amzn (:db request)
                 existing (attr/fetch amzn
                                      (:attribute-id (:params request)))]
             (when-not existing
               (attr/store amzn
                           (loadchoices
                            {:attribute-id (:attribute-id (:params request))
                             :label (:label (:params request))
                             :type (:type (:params request))
                             :order (parse-int (:order (:params request)))}
                            (:params request)))))
           {:location (str "/attribute/" (:attribute-id (:params request)))})
  :post-redirect? true

  :handle-ok (fn [_]
               (view/new-attribute {})))

(liberator/defresource edit-attribute
  :exists? (fn [{:keys [request]}]
             (let [amzn (:db request)
                   attribute-id (:attribute-id request)
                   attribute (attr/fetch amzn attribute-id)]
               [(boolean attribute)
                {:attribute attribute}]))
  :handle-not-found (fn [{:keys [request]}]
                      (view/not-found (:uri request)))
  :available-media-types ["text/html"]
  :available-charsets ["utf-8"]
  :handle-ok (fn [context]
               (view/edit-attribute (:attribute context))))

(liberator/defresource attribute
  :allowed-methods [:get :head :post :delete]
  :exists? (fn [{:keys [request]}]
             (let [amzn (:db request)
                   attribute-id (:attribute-id request)
                   attribute (attr/fetch amzn attribute-id)]
               [(boolean attribute)
                {:attribute attribute}]))
  :post! (fn [{:keys [request]}]
           (attr/store (:db request)
                       (loadchoices
                        {:attribute-id (:attribute-id (:params request))
                         :label (:label (:params request))
                         :type (:type (:params request))
                         :order (parse-int (:order (:params request)))}
                        (:params request)))
           {:location (str "/attribute/" (:attribute-id (:params request)))})
  :post-redirect? true

  :delete! (fn [{:keys [request]}]
             (attr/delete (:db request) (:attribute-id request)))
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
               (view/list-attributes (attr/all (:db (:request context))))))

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
