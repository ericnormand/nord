(ns nord.view
  (:use hiccup.core)
  (:use hiccup.page)
  (:require [clj-json.core          :as json]))

(defn cc [test & body]
  (concat 
   [(str "<!--[if " test "]>")]
   body
   ["<![endif]-->"]))

(defn page [pg & rst]
  (html5
   [:head
    [:meta {:http-equiv "Content-Type" :content "text/html; charset=UTF-8"}]
    [:meta {:http-equiv "X-UA-Compatible" :content "IE=edge,chrome=1"}]
    [:title (:title pg)]
    [:meta {:name "description"
            :content (:description pg)}]
    [:meta {:name "viewport" :content "width=device-width, initial-scale=1.0, user-scalable=no"}]

    (cc "lt IE 9"
        (include-js "http://html5shiv.googlecode.com/svn/trunk/html5.js"))
    (include-css "http://code.jquery.com/mobile/1.2.0/jquery.mobile-1.2.0.min.css")
    (include-css "/bootstrap/css/bootstrap.min.css")
    (include-css "/bootstrap/css/bootstrap-responsive.min.css")
    (include-css "http://fonts.googleapis.com/css?family=Lato:300,400,700")
    (include-css "/css/style.css")]

   [:body.home
    (cc "lt IE 7"
        [:p.chromeframe
         "Your browser is " [:em "ancient!"]
         [:a {:href "http://browsehappy.com/"} "Upgrade to a different browser"]
         " or "
         [:a {:href "http://www.google.com/chromeframe/?redirect=true"} "install Google Chrome Frame"] " to experience this site."])
    
    [:header
     [:a {:src "/"}
      [:img.logo {:src "/img/logo.png"}]]]
    
    [:article
     [:div.middle
      [:div.inner
       rst]]]
    
    [:footer]
    (include-js "http://maps.google.com/maps/api/js?sensor=true")
    (include-js "//ajax.googleapis.com/ajax/libs/jquery/1.7.1/jquery.min.js")
    (include-js "//ajax.googleapis.com/ajax/libs/jqueryui/1.9.2/jquery-ui.min.js")    
    (include-js "/js/jquery.ui.map.full.min.js")
    (include-js "/js/jquery.ui.map.services.js")
    (include-js "/js/jquery.ui.map.extensions.js")

    ;;(include-js "/bootstrap/js/bootstrap.min.js")
    (include-js "/js/script.js")
    ;; put google analytics stuff here when you're ready
    ]))

(defn app [pg & rst]
  (html5
   [:head
    [:meta {:http-equiv "Content-Type" :content "text/html; charset=UTF-8"}]
    [:meta {:http-equiv "X-UA-Compatible" :content "IE=edge,chrome=1"}]
    [:title (:title pg)]
    [:meta {:name "description"
            :content (:description pg)}]
    [:meta {:name "viewport" :content "width=device-width, initial-scale=1.0, user-scalable=no"}]

    (cc "lt IE 9"
        (include-js "http://html5shiv.googlecode.com/svn/trunk/html5.js"))
    (include-css "http://code.jquery.com/mobile/1.2.0/jquery.mobile-1.2.0.min.css")
    (include-css "/bootstrap/css/bootstrap.min.css")
    (include-css "/bootstrap/css/bootstrap-responsive.min.css")
    (include-css "http://fonts.googleapis.com/css?family=Lato:300,400,700")
    (include-css "/css/style.css")]

   [:body.home
    (cc "lt IE 7"
        [:p.chromeframe
         "Your browser is " [:em "ancient!"]
         [:a {:href "http://browsehappy.com/"} "Upgrade to a different browser"]
         " or "
         [:a {:href "http://www.google.com/chromeframe/?redirect=true"} "install Google Chrome Frame"] " to experience this site."])
    
    [:header
     [:img.sel {:src "/img/arrows.png"}]
     [:img.logo {:src "/img/logo.png"}]
     [:img.list {:src "/img/list_text.png"}]]
    
    [:article
     [:div.middle
      [:div.inner
       rst]]]
    
    [:footer]
    (include-js "http://maps.google.com/maps/api/js?sensor=true")
    (include-js "//ajax.googleapis.com/ajax/libs/jquery/1.7.1/jquery.min.js")
    (include-js "//ajax.googleapis.com/ajax/libs/jqueryui/1.9.2/jquery-ui.min.js")    
    (include-js "/js/jquery.ui.map.full.min.js")
    (include-js "/js/jquery.ui.map.services.js")
    (include-js "/js/jquery.ui.map.extensions.js")

    ;;(include-js "/bootstrap/js/bootstrap.min.js")
    (include-js "/js/script.js")
    ;; put google analytics stuff here when you're ready
    ]))

(def selectors
  [   {:attribute "historic"
    :title "Historic"
    :icon "/img/icon/monument.png"}
   {:attribute "festival-space"
    :title "Festival space"
    :icon "/img/icon/festival.png"}
   {:attribute "all-purpose-field"
    :title "Field"
    :icon "/img/icon/field.png"}
   {:attribute "playground"
    :title "Playground"
    :icon "/img/icon/playground.png"}
   {:attribute "tennis"
    :title "Tennis"
    :icon "/img/icon/tennis.png"}
   {:attribute "swimming-pool"
    :title "Swim"
    :icon "/img/icon/swim.png"}
   {:attribute "restrooms"
    :title "Restroom"
    :icon "/img/icon/wc.png"}
   {:attribute "water-fountain"
    :title "Water fountain"
    :icon "/img/icon/waterfountain.png"}
   {:attribute "showers"
    :title "Showers"
    :icon "/img/icon/shower.png"}
   {:attribute "lighting"
    :title "Lighting"
    :icon "/img/icon/light.png"}
   {:attribute "batting-cage"
    :title "Batting cages"
    :icon "/img/icon/baseball.png"}
   {:attribute "track-field"
    :title "Track field"
    :icon "/img/icon/trackfield.png"}
   {:attribute "basketball"
    :title "Basketball"
    :icon "/img/icon/basket.png"}
   {:attribute "rec-center"
    :title "Rec center"
    :icon "/img/icon/reccenter.png"}
   {:attribute "parking"
    :title "Parking"
    :icon "/img/icon/parking.png"}
   {:attribute "picnic"
    :title "Picnic tables"
    :icon "/img/icon/picnic.png"}])

(defn selector-list []
  [:div.selector-list
   [:div {:data-attr "all"}
    [:span "All"]]
   (for [s selectors]
     [:div.selected {:data-attr (:attribute s)}
      [:img {:src (:icon s)}]
      [:span (:title s)]])])

(def neighborhoods
  ["Uptown"
   "Algiers"
   "Central Business District"
   "Gentilly/New Orleans East"
   "Lakeview"
   "Downtown"])

(defn n-park-template []
  [:li.park
   [:a.name {:href "#"}]])

(defn neighborhood-template []
  [:h3
   [:span.name]
   [:span.number]]
  [:ul.parks])

(defn neighborhood-list [parks]
  [:div.neigh-list
   ])

(defn attribute-template []
  [:div.attribute
   [:img.icon]
   [:span.txt]])

(defn park-view-template []
  [:div.park-view
   [:header
    [:img.big-img]
    [:span.name]]
   [:div.body
    [:button.close "&times;"]
    [:div.address]
    [:div.city "New Orleans, LA"]
    [:div.attributes]
    ]])

(defn homepage [cfg]
  (app {:title "NORD"
        :description "Explore NOLA outdoors."}
       (comment
         [:div
          [:a {:href "/location/list"} "All locations"]])
       (comment
         [:div
          [:a {:href "/attribute/list"} "All attributes"]])

       [:script
        "window.attributes = "
        (json/generate-string selectors)
        ";\n\n"
        "window.neighborhoods = "
        (json/generate-string neighborhoods)
        ]

       [:div#map-view]

       (selector-list)
       (neighborhood-list (:parks cfg))

       [:script.park-view-template
        (park-view-template)]

       [:script.attribute-template
        (attribute-template)]

       [:script.neighborhood-template
        (neighborhood-template)]

       [:script.n-park-template
        (n-park-template)]))

(defn park [fields park]
  (page {:title (:name park)}
        [:a {:href (str "/location/" (:park-id park))}
         [:h2 (:name park)]]
        [:a.btn.btn-primary {:href (str "/location/" (:park-id park) "/edit")}
         "Edit"]
        [:a {:href "/location/list"} "Complete list"]
        [:div
         [:div.row
          [:div.span2.align-right
           "Park ID"]
          [:div.span3
           (:park-id park)]]
         (for [field fields
               :when (park (keyword (:attribute-id field)))]
           [:div.row
            [:div.span2.align-right
             (:label field)]
            [:div.span3
             (park (keyword (:attribute-id field)))]])]))

(defn not-found [uri]
  (page {:title "Not found"}
        "Not found: " uri))

(defn list-parks [parks]
  (page {:title "All locations"}
        [:a.btn.btn-primary {:href "/location/new"} "New"]
        [:ul
         (for [park parks]
           [:li [:a {:href (str "/location/" (:park-id park))}
                 (:name park)]])]))

(defn build-populate-input [park field]
  (case (:type field)
    "text" [:div.control-group
            [:label.control-label {:for (str "input-" (:attribute-id field))}
             (:label field)]
            [:div.controls
             [:input {:type "text"
                      :name (:attribute-id field)
                      :id (str "input-" (:attribute-id field))
                      :value (park (keyword (:attribute-id field)))}
              ]]]
    "choices" [:div.control-group
               [:label.control-label {:for (str "input-" (:attribute-id field))}
                (:label field)]
               [:div.controls
                [:select {:name (:attribute-id field)
                          :id (str "input-" (:attribute-id field))}
                 [:option {:value ""}
                  "Unknown"]
                 (for [choice (:choices field)]
                   [:option (when (= choice (park (keyword (:attribute-id field))))
                              {:selected "selected"})
                    choice])]]]
    "image" [:div.control-group
             [:label.control-label {:for (str "input-" (:attribute-id field))}
              (:label field) " (url)"]
             [:div.controls
              [:input {:type "text"
                       :name (:attribute-id field)
                       :id (str "input-" (:attribute-id field))
                       :value (park (keyword (:attribute-id field)))}]]]
    "checkbox" [:div.control-group
                [:label.control-label]
                [:label.checkbox
                 [:input (if (park (keyword (:attribute-id field)))
                           {:type "checkbox"
                            :name (:attribute-id field)
                            :checked "checked"}
                           {:type "checkbox"
                            :name (:attribute-id field)})]
                 (:label field)]]))

(defn edit-park [fields park]
  (page {:title (:name park)}
        [:form.form-horizontal {:method "POST"
                                :action (str "/location/" (:park-id park))}
         [:div.control-group
          [:label.control-label {:for "input-park-id"}
           "Park ID"]
          [:div.controls
           [:input {:type "text"
                    :name "park-id"
                    :id "input-park-id"
                    :value (:park-id park)}]]]
         (map #(build-populate-input park %) fields)
         [:div.control-group
          [:div.controls
           [:input {:type "submit"
                    :class "btn btn-primary"
                    :value "Save"}]]]]))

(defn build-input [field]
  (case (:type field)
    "text" [:div.control-group
            [:label.control-label {:for (str "input" (:name field))}
             (:label field)]
            [:div.controls
             [:input {:type "text"
                      :name (:name field)
                      :id (str "input" (:name field))}]]]
    "choices" [:div.control-group
               [:label.control-label {:for (str "input" (:name field))}
                (:label field)]
               [:div.controls
                [:select {:name (:name field)
                          :id (str "input" (:name field))}
                 [:option {:value ""}
                  "Unknown"]
                 (for [choice (:choices field)]
                   [:option choice])]]]
    "image" [:div.control-group
             [:label.control-label {:for (str "input-" (:attribute-id field))}
              (:label field) " (url)"]
             [:div.controls
              [:input {:type "text"
                       :name (:attribute-id field)
                       :id (str "input-" (:attribute-id field))}]]]
    "checkbox" [:div.control-group
                [:label.checkbox
                 [:input {:type "checkbox"
                          :name (:attribute-id field)}]
                 (:label field)]]))

(defn new-park [fields park]
  (page {:title "New park"}
        [:h2 "New park"]
        [:form.form-horizontal {:method "POST"}
         [:div.control-group
          [:label.control-label {:for "input-park-id"}
           "Park ID"]
          [:div.controls
           [:input {:type "text"
                    :name "park-id"
                    :id "input-park-id"}]]]
         (map build-input fields)
         [:div.control-group
          [:div.controls
           [:input {:type "submit"
                    :class "btn btn-primary"
                    :value "Save"}]]]]))

(defn edit-attribute [attribute]
  (page {:title (:label attribute)}
        [:a {:href (str "/attribute/" (:attribute-id attribute))}
         [:h2 (:label attribute)]]
        [:form.form-horizontal {:method "POST"
                                :action (str "/attribute/" (:attribute-id attribute))}
         [:input {:type "hidden"
                  :name "__method"
                  :value "PUT"}]
         [:div.control-group
          [:label.control-label {:for "input-name"}
           "Name"]
          [:div.controls
           [:input {:type "text"
                    :id "input-name"
                    :name "attribute-id"
                    :placeholder "Name"
                    :value (:attribute-id attribute)}]]]
         [:div.control-group
          [:label.control-label {:for "input-label"}
           "Label"]
          [:div.controls
           [:input {:type "text"
                    :id "input-label"
                    :name "label"
                    :placeholder "Label"
                    :value (:label attribute)}]]]
         [:div.control-group
          [:label.control-label {:for "input-order"}
           "Order"]
          [:div.controls
           [:input {:type "text"
                    :id "input-order"
                    :name "order"
                    :placeholder "Order (number)"
                    :value (:order attribute)}]]]
         [:div.control-group
          [:label.control-label {:for "input-type"}
           "Type"]
          [:div.controls
           [:select {:id "input-type"
                     :name "type"}
            [:option (if (= "text" (:type attribute))
                       {:selected "selected"
                        :value "text"}
                       {:value "text"})
             "Text"]
            [:option (if (= "choices" (:type attribute))
                       {:selected "selected"
                        :value "choices"}
                       {:value "choices"})
             "Choices"]
            [:option {:value "image"}
             "Image"]
            [:option {:value "checkbox"}
             "Yes/No"]]]]
         
         [:div.extra-controls
          (when (= "choices" (:type attribute))
            [:div.control-group
             [:label.control-label {:for "input-choices"}
              "Choices"]
             [:div.controls
              [:i#add-choice {:class "icon-plus pull-right"}]
              (for [choice (:choices attribute)]
                [:input {:type "text"
                         :name "choice"
                         :placeholder "Choice name"
                         :value choice}])]])]

         [:div.control-group
          [:div.controls
           [:input {:type "submit"
                    :class "btn btn-primary"
                    :value "Save"}]]]]))

(defn new-attribute [attribute]
  (page {:title "New attribute"}
        [:h2 "New attribute"]
        [:form.form-horizontal {:method "POST"}
         [:div.control-group
          [:label.control-label {:for "input-name"}
           "Name"]
          [:div.controls
           [:input {:type "text"
                    :id "input-name"
                    :name "attribute-id"
                    :placeholder "Name"}]]]
         [:div.control-group
          [:label.control-label {:for "input-label"}
           "Label"]
          [:div.controls
           [:input {:type "text"
                    :id "input-label"
                    :name "label"
                    :placeholder "Label"}]]]
         
         [:div.control-group
          [:label.control-label {:for "input-order"}
           "Order"]
          [:div.controls
           [:input {:type "text"
                    :id "input-order"
                    :name "order"
                    :placeholder "Order (number)"}]]]
         [:div.control-group
          [:label.control-label {:for "input-type"}
           "Type"]
          [:div.controls
           [:select {:id "input-type"
                     :name "type"}
            [:option {:selected "selected"
                      :value "text"}
             "Text"]
            [:option {:value "choices"}
             "Choices"]
            [:option {:value "image"}
             "Image"]
            [:option {:value "checkbox"}
             "Yes/No"]]]]
         
         [:div.extra-controls
          
          ]

         [:div.control-group
          [:div.controls
           [:input {:type "submit"
                    :class "btn btn-primary"
                    :value "Save"}]]]]

        [:script#choices
         [:div.control-group
          [:label.control-label {:for "input-choices"}
           "Choices"]
          [:div.controls
           [:i#add-choice {:class "icon-plus pull-right"}]]]]
        
        [:script#choice
         [:input {:type "text"
                  :name "choice"
                  :placeholder "Choice name"}]]))

(defn attribute [attribute]
  (page {:title (:label attribute)}
        [:h2 (:label attribute)]
        [:a.btn.btn-primary {:href (str "/attribute/" (:attribute-id attribute) "/edit")}
         "Edit"]
        [:a.btn.btn-primary {:href "/attribute/new"}
         "New"]
        [:div
         [:div.row
          [:div.span2.align-right
           "Name"]
          [:div.span3
           (:attribute-id attribute)]]
         [:div.row
          [:div.span2.align-right
           "Label"]
          [:div.span3
           (:label attribute)]]
         [:div.row
          [:div.span2.align-right
           "Order"]
          [:div.span3
           (:order attribute)]]
         [:div.row
          [:div.span2.align-right
           "Type"]
          [:div.span3
           (:type attribute)
           (when (= (:type attribute) "choices")
             (for [choice (:choices attribute)]
               [:div choice]))]]]))

(defn list-attributes [attributes]
  (page {:title "All attributes"}
        [:a.btn.btn-primary {:href "/attribute/new"} "New"]
        [:ul
         (for [attribute attributes]
           [:li [:a {:href (str "/attribute/" (:attribute-id attribute))}
                 (:label attribute)]])]))