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

    
    (include-css "http://code.jquery.com/mobile/1.2.0/jquery.mobile-1.2.0.min.css")
    (include-css "/bootstrap/css/bootstrap.min.css")
    (include-css "/bootstrap/css/bootstrap-responsive.min.css")
    (include-css "http://fonts.googleapis.com/css?family=Lato:300,400,700")
    (include-css "/css/style.css")
    (cc "lt IE 9"
        (include-js "http://html5shiv.googlecode.com/svn/trunk/html5.js"))
    "<!-- start Mixpanel -->"
    [:script
     "(function(e,b){if(!b.__SV){var a,f,i,g;window.mixpanel=b;a=e.createElement(\"script\");a.type=\"text/javascript\";a.async=!0;a.src=(\"https:\"===e.location.protocol?\"https:\":\"http:\")+'//cdn.mxpnl.com/libs/mixpanel-2.2.min.js';f=e.getElementsByTagName(\"script\")[0];f.parentNode.insertBefore(a,f);b._i=[];b.init=function(a,e,d){function f(b,h){var a=h.split(\".\");2==a.length&&(b=b[a[0]],h=a[1]);b[h]=function(){b.push([h].concat(Array.prototype.slice.call(arguments,0)))}}var c=b;\"undefined\"!==
typeof d?c=b[d]=[]:d=\"mixpanel\";c.people=c.people||[];c.toString=function(b){var a=\"mixpanel\";\"mixpanel\"!==d&&(a+=\".\"+d);b||(a+=\" (stub)\");return a};c.people.toString=function(){return c.toString(1)+\".people (stub)\"};i=\"disable track track_pageview track_links track_forms register register_once alias unregister identify name_tag set_config people.set people.increment people.append people.track_charge people.clear_charges people.delete_user\".split(\" \");for(g=0;g<i.length;g++)f(c,i[g]);b._i.push([a,
e,d])};b.__SV=1.2}})(document,window.mixpanel||[]);
mixpanel.init(\"cadf3f46a8cd9160c6ad9722440db0fc\");"]
    "<!-- end Mixpanel -->"]

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
    
    [:article.edit
     [:div.middle
      [:div.inner
       rst]]]
    
    [:footer]
    (include-js "http://maps.google.com/maps/api/js?sensor=true")
    (include-js "//ajax.googleapis.com/ajax/libs/jquery/1.7.1/jquery.min.js")
    (comment (include-js "//ajax.googleapis.com/ajax/libs/jqueryui/1.9.2/jquery-ui.min.js"))    
    (include-js "/js/jquery.ui.map.full.min.js")
    (include-js "/js/jquery.ui.map.services.js")
    (include-js "/js/jquery.ui.map.extensions.js")

    (include-js "/bootstrap/js/bootstrap.min.js")
    (include-js "/js/script.js")
    ;; put google analytics stuff here when you're ready
    [:script "

  var _gaq = _gaq || [];
  _gaq.push(['_setAccount', 'UA-37632091-1']);
  _gaq.push(['_setDomainName', 'nolaparks.com']);
  _gaq.push(['_trackPageview']);

  (function() {
    var ga = document.createElement('script'); ga.type = 'text/javascript'; ga.async = true;
    ga.src = ('https:' == document.location.protocol ? 'https://ssl' : 'http://www') + '.google-analytics.com/ga.js';
    var s = document.getElementsByTagName('script')[0]; s.parentNode.insertBefore(ga, s);
  })();"]
    ]))

(defn app [pg & rst]
  (html5
   [:head
    [:meta {:http-equiv "Content-Type" :content "text/html; charset=UTF-8"}]
    [:meta {:http-equiv "X-UA-Compatible" :content "IE=edge,chrome=1"}]
    [:title (:title pg)]
    [:meta {:name "description"
            :content "Your guide to Parks and Recreation centers in New Orleans, Louisiana."}]
    [:meta {:name "viewport" :content "width=device-width, initial-scale=1.0, user-scalable=no, maximum-scale=1.0"}]
    [:meta {:name "apple-mobile-web-app-capable" :content "yes"}]
    
    [:meta {:property "og:title"
            :content "Nola Parks - Explore New Orleans Outdoors"}]
    [:meta {:property "og:description"
            :content "NolaParks.com is your guide to outdoor and sports activities in New Orleans. Easily find parks, recreation centers, and other outdoor spaces all from your phone. Go explore New Orleans outdoors!"}]
    [:meta {:property "og:url"
            :content "http://www.nolaparks.com/"}]
    [:meta {:property "og:image"
            :content "https://dl.dropbox.com/s/r3t3a5go03u2e35/Square-Logo.png"}]
    
    [:link {:rel "shortcut icon"
            :href "/img/fav.png"}]
    
    (include-css "http://code.jquery.com/mobile/1.2.0/jquery.mobile-1.2.0.min.css")
    (include-css "/bootstrap/css/bootstrap.min.css")
    (include-css "/bootstrap/css/bootstrap-responsive.min.css")
    (include-css "http://fonts.googleapis.com/css?family=Lato:300,400,700")
    (include-css "/css/jquery.tweet.css")
    (include-css "/css/add2home.css")
    (include-css "/css/style.css")
    (cc "lt IE 9"
        (include-js "http://html5shiv.googlecode.com/svn/trunk/html5.js"))
    (include-js "/js/modernizr.js")

    "<!-- start Mixpanel -->"
    [:script
     "(function(e,b){if(!b.__SV){var a,f,i,g;window.mixpanel=b;a=e.createElement(\"script\");a.type=\"text/javascript\";a.async=!0;a.src=(\"https:\"===e.location.protocol?\"https:\":\"http:\")+'//cdn.mxpnl.com/libs/mixpanel-2.2.min.js';f=e.getElementsByTagName(\"script\")[0];f.parentNode.insertBefore(a,f);b._i=[];b.init=function(a,e,d){function f(b,h){var a=h.split(\".\");2==a.length&&(b=b[a[0]],h=a[1]);b[h]=function(){b.push([h].concat(Array.prototype.slice.call(arguments,0)))}}var c=b;\"undefined\"!==
typeof d?c=b[d]=[]:d=\"mixpanel\";c.people=c.people||[];c.toString=function(b){var a=\"mixpanel\";\"mixpanel\"!==d&&(a+=\".\"+d);b||(a+=\" (stub)\");return a};c.people.toString=function(){return c.toString(1)+\".people (stub)\"};i=\"disable track track_pageview track_links track_forms register register_once alias unregister identify name_tag set_config people.set people.increment people.append people.track_charge people.clear_charges people.delete_user\".split(\" \");for(g=0;g<i.length;g++)f(c,i[g]);b._i.push([a,
e,d])};b.__SV=1.2}})(document,window.mixpanel||[]);
mixpanel.init(\"cadf3f46a8cd9160c6ad9722440db0fc\");"]
    "<!-- end Mixpanel -->"

    [:link {:rel "apple-touch-icon" :sizes "144x144" :href "http://www.nolaparks.com/nolaparks_icon.png"}]
    [:meta {:name "apple-mobile-web-app-status-bar-style" :content "black"}]
    ]

   [:body.total
    (cc "lt IE 7"
        [:p.chromeframe
         "Your browser is " [:em "ancient!"]
         [:a {:href "http://browsehappy.com/"} "Upgrade to a different browser"]
         " or "
         [:a {:href "http://www.google.com/chromeframe/?redirect=true"} "install Google Chrome Frame"] " to experience this site."])

    [:div.wrapper
     [:header
      [:img.sel {:src "/img/arrows.png"}]
      [:div.logo]
      [:img.list {:src "/img/list_text.png"}]
      [:img.map {:src "/img/map_text.png"}]]
     
     [:article
      [:div.middle
       [:div.inner
        rst]]]
     
     [:footer]]
    (include-js "http://maps.google.com/maps/api/js?sensor=true")
    (include-js "//ajax.googleapis.com/ajax/libs/jquery/1.7.1/jquery.min.js")
    (include-js "//ajax.googleapis.com/ajax/libs/jqueryui/1.9.2/jquery-ui.min.js")    
    (include-js "/js/jquery.ui.map.full.min.js")
    (include-js "/js/jquery.ui.map.services.js")
    (include-js "/js/jquery.ui.map.extensions.js")
    (include-js "/js/jquery.tweet.js")
    (include-js "/bootstrap/js/bootstrap.min.js")
    (include-js "/js/jquery.ba-bbq.min.js")
    (include-js "/js/iscroll.js")
    (include-js "/js/add2home.js")
    (include-js "/js/script.js")
    ;; put google analytics stuff here when you're ready
    [:script "

  var _gaq = _gaq || [];
  _gaq.push(['_setAccount', 'UA-37632091-1']);
  _gaq.push(['_setDomainName', 'nolaparks.com']);
  _gaq.push(['_trackPageview']);

  (function() {
    var ga = document.createElement('script'); ga.type = 'text/javascript'; ga.async = true;
    ga.src = ('https:' == document.location.protocol ? 'https://ssl' : 'http://www') + '.google-analytics.com/ga.js';
    var s = document.getElementsByTagName('script')[0]; s.parentNode.insertBefore(ga, s);
  })();"]
    ]))

(def features
  [{:attribute "running"
    :title "Running"
    :icon "/img/icon/running.png"}
   {:attribute "playground"
    :title "Playground"
    :icon "/img/icon/playground.png"}
   {:attribute "dog-park"
    :title "Dog park"
    :icon "/img/icon/dogpark.png"}
   {:attribute "tennis"
    :title "Tennis"
    :icon "/img/icon/tennis.png"}
   {:attribute "basketball"
    :title "Basketball"
    :icon "/img/icon/basketball.png"}
   {:attribute "bicycle"
    :title "Bicycle"
    :icon "/img/icon/bicycle.png"}
   {:attribute "picnic-tables"
    :title "Picnic tables"
    :icon "/img/icon/picnic.png"}
   {:attribute "all-purpose-field"
    :title "Sports field"
    :icon "/img/icon/sportsfield.png"}
   
   {:attribute "historic"
    :title "Sightseeing"
    :icon "/img/icon/monument.png"}
   {:attribute "swimming-pool"
    :title "Swimming"
    :icon "/img/icon/swimming.png"}
   {:attribute "super-saturday"
    :title "Super Saturday"
    :icon "/img/icon/supersaturday.png"}])

(def selectors
  (concat features
          
          [{:attribute "restrooms"
            :title "Restroom"
            :icon "/img/icon/restroom.png"}
           {:attribute "showers"
            :title "Showers"
            :icon "/img/icon/shower.png"}
           {:attribute "lighting"
            :title "Lighting"
            :icon "/img/icon/lights.png"}]))

(defn selector-list []
  [:div#selector-list.selector-list
   [:div.selector-inner
    [:ul.nav.nav-list
     [:li.filter.active
      [:a {:href "#"
           :data-attr "all"}
       [:img {:src "/img/icon/viewall.png"}]
       [:span "All"]]]
     (for [s features]
       [:li.filter.active
        [:a {:href "#"
             :data-attr (:attribute s)}
         [:img {:src (:icon s)}]
         [:span (:title s)]]])]
    
    [:div.about
     [:a {:href "/about.html"}
      "About"]]
     ]
   
   ])

(def neighborhoods
  ["Algiers"
   "Downtown"
   "Gentilly/New Orleans East"
   "Lakeview"
   "Uptown"])

(defn n-park-template []
  [:li.park
   [:a.name {:href "#"}]])

(defn neighborhood-template []
  [:div.accordion-group
   [:div.accordion-heading
    [:a.accordion-toggle
     {:data-toggle "collapse"
      :data-parent ".neigh-list"
      :href "#collapseOne"}
     [:span.name]
     [:span.number]]]
   [:div.accordionBody.collapse
    [:div.accordion-inner
     (comment
       [:form.form-search
        [:div.input-append
         [:input.input-medium.search-query.neigh-filter
          {:type "text"
           :placeholder "Type here to filter list"}]
         [:button.btn
          "Clear"]]])
     [:ul.parks]]]])

(defn neighborhood-list [parks]
  [:div#nl.neigh-list
   [:div.accordion
    ]])

(defn attribute-template []
  [:div.attribute
   [:img.icon]
   [:span.txt]])

(defn park-view-template []
  [:div.park-view
   [:button.close "&times;"]
   [:header
    [:img.big-img]
    [:span.name]]
   [:div#park-view-wrapper
    [:div.body
     [:a.location-block {:target "_blank"}
      [:img.map_icon.pull-left {:src "/img/map_icon.png"}]
      [:div.name]
      [:div.address]]
     [:h3.hoursofoperation "Hours of Operation"]
     [:div.hoursofoperation]
     [:a.website {:targer "_blank"}]
     [:h3.attributes "Activities"]
     [:div.attributes]
     [:h3.subattributes "Amenities"]
     [:ul.subattributes]
     [:h3 "On Twitter"]
     [:div.twitter]]]])

(defn homepage [cfg]
  (app {:title "NOLA Parks"
        :description "Explore NOLA outdoors."}
       (comment
         [:div
          [:a {:href "/location/list"} "All locations"]])
       (comment
         [:div
          [:a {:href "/attribute/list"} "All attributes"]])

       [:script
        "window.neighborhoods = "
        (json/generate-string neighborhoods)
        ";\n\n"
        "window.parks = "
        (json/generate-string (:parks cfg))
        ";\n\n\n"
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
        [:a.btn.btn-primary {:href "/location/new"} "New"]
        [:a.btn.btn-primary {:href "/location/list"} "List"]
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
        [:a.btn.btn-primary {:href "/attribute/list"} "Attributes"]
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
                      :value (park (keyword (:attribute-id field)))}]]]
    "number" [:div.control-group
              [:label.control-label {:for (str "input-" (:attribute-id field))}
               (:label field)]
              [:div.controls
               [:input {:type "text"
                        :name (:attribute-id field)
                        :id (str "input-" (:attribute-id field))
                        :value (park (keyword (:attribute-id field)))}]]]
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
    "checkbox" [:div.control-group
                [:label.control-label {:for (str "input-" (:attribute-id field))}
                 (:label field)]
                [:div.controls
                 [:input (if (park (keyword (:attribute-id field)))
                           {:type "checkbox"
                            :name (:attribute-id field)
                            :checked "checked"}
                           {:type "checkbox"
                            :name (:attribute-id field)})]]]))

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
                    :value "Save"}]
           [:a.btn.btn-danger {:href (str "/location/" (:park-id park))}
            "Cancel"]]]]))

(defn build-input [field]
  (case (:type field)
    "text" [:div.control-group
            [:label.control-label {:for (str "input" (:attribute-id field))}
             (:label field)]
            [:div.controls
             [:input {:type "text"
                      :name (:attribute-id field)
                      :id (str "input" (:attribute-id field))}]]]
    "number" [:div.control-group
              [:label.control-label
               {:for (str "input" (:attribute-id field))}
               (:label field) " (number)"]
              [:div.controls
               [:input {:type "text"
                        :name (:attribute-id field)
                        :id (str "input" (:attribute-id field))}]]]
    "choices" [:div.control-group
               [:label.control-label {:for (str "input" (:attribute-id field))}
                (:label field)]
               [:div.controls
                [:select {:name (:attribute-id field)
                          :id (str "input" (:attribute-id field))}
                 [:option {:value ""}
                  "Unknown"]
                 (for [choice (:choices field)]
                   [:option choice])]]]
    "checkbox" [:div.control-group
                [:label.control-label {:for (str "input" (:attribute-id field))}
                 (:label field)]
                [:div.controls
                 [:input {:type "checkbox"
                          :id (str "input" (:attribute-id field))
                          :name (:attribute-id field)}]]]))

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
                    :value "Save"}]
           [:a.btn.btn-danger {:href "/location/list"}
            "Cancel"]
           ]]]))

(defn edit-attribute [attribute]
  (page {:title (:label attribute)}
        [:a {:href (str "/attribute/" (:attribute-id attribute))}
         [:h2 (:label attribute)]]
        [:form.form-horizontal {:method "POST"
                                :action (str "/attribute/" (:attribute-id attribute))}
         [:div.control-group
          [:label.control-label {:for "input-name"}
           "Attribute ID"]
          [:div.controls
           [:input {:type "text"
                    :id "input-name"
                    :name "attribute-id"
                    :placeholder "Attribute ID"
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
           "Order (number)"]
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
            [:option (if (= "number" (:type attribute))
                       {:selected "selected"
                        :value "number"}
                       {:value "number"})
             "Number"]
            [:option (if (= "choices" (:type attribute))
                       {:selected "selected"
                        :value "choices"}
                       {:value "choices"})
             "Choices"]
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
                    :value "Save"}]
           [:a.btn.btn-danger {:href (str "/attribute/" (:attribute-id attribute))}
            "Cancel"]]]]))

(defn new-attribute [attribute]
  (page {:title "New attribute"}
        [:h2 "New attribute"]
        [:form.form-horizontal {:method "POST"}
         [:div.control-group
          [:label.control-label {:for "input-name"}
           "Attribute ID"]
          [:div.controls
           [:input {:type "text"
                    :id "input-name"
                    :name "attribute-id"
                    :placeholder "Attribute ID"}]]]
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
           "Order (number)"]
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
            [:option {:value "number"}
             "Number"]
            [:option {:value "choices"}
             "Choices"]
            [:option {:value "checkbox"}
             "Yes/No"]]]]
         
         [:div.extra-controls
          
          ]

         [:div.control-group
          [:div.controls
           [:input {:type "submit"
                    :class "btn btn-primary"
                    :value "Save"}]
           [:a.btn.btn-danger {:href "/attribute/list"}
            "Cancel"]]]]

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
        [:a.btn.btn-primary {:href "/attribute/list"}
         "List"]
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
        [:a.btn.btn-primary {:href "/location/list"} "Locations"]
        [:ul
         (for [attribute attributes]
           [:li [:a {:href (str "/attribute/" (:attribute-id attribute))}
                 (:label attribute)]])]))
