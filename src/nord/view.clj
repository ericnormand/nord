(ns nord.view
  (:use hiccup.core)
  (:use hiccup.page))

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
    [:meta {:name "viewport" :content "width=device-width, initial-scale=1.0"}]

    (cc "lt IE 9"
        (include-js "http://html5shiv.googlecode.com/svn/trunk/html5.js"))

    (include-css "/bootstrap/css/bootstrap.min.css")
    (include-css "/bootstrap/css/bootstrap-responsive.min.css")
    (include-css "/css/style.css")]

   [:body.home
    (cc "lt IE 7"
        [:p.chromeframe
         "Your browser is " [:em "ancient!"]
         [:a {:href "http://browsehappy.com/"} "Upgrade to a different browser"]
         " or "
         [:a {:href "http://www.google.com/chromeframe/?redirect=true"} "install Google Chrome Frame"] " to experience this site."])
    
    [:header
     [:h1 "Welcome to NORD!"]]
    
    [:article
     [:div.middle
      [:div.inner
       rst
       ]]]
    
    [:footer]
    (include-js "//ajax.googleapis.com/ajax/libs/jquery/1.8.2/jquery.min.js")
    [:script
     "window.jQuery || document.write('<script src=\"/js/jquery-1.8.2.min.js\"><\\/script>')"]
    (include-js "/bootstrap/js/bootstrap.min.js")
    (include-js "/js/script.js")
    ;; put google analytics stuff here when you're ready
    ]))

(defn homepage [cfg]
  (page {:title "NORD"
         :description "Explore NOLA outdoors."}
        [:div
         [:a {:href "/location/list"} "All locations"]]
        [:div
         [:a {:href "/attribute/list"} "All attributes"]]))

(defn park [fields park]
  (prn fields)
  (page {:title (:name park)}
        [:h2 (:name park)]
        [:a.btn.btn-primary {:href (str "/location/" (:park-id park) "/edit")}
         "Edit"]
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
                    choice])]]]))

(defn edit-park [fields park]
  (prn park)

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
                   [:option choice])]]]))

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
             "Choices"]]]]
         
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
          [:label.control-label {:for "input-label"}
           "Label"]
          [:div.controls
           [:input {:type "text"
                    :id "input-label"
                    :name "label"
                    :placeholder "Label"}]]]
         [:div.control-group
          [:label.control-label {:for "input-name"}
           "Name"]
          [:div.controls
           [:input {:type "text"
                    :id "input-name"
                    :name "attribute-id"
                    :placeholder "Name"}]]]
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
             "Choices"]]]]
         
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
        [:div
         [:div.row
          [:div.span2.align-right
           "Label"]
          [:div.span3
           (:label attribute)]]
         [:div.row
          [:div.span2.align-right
           "Name"]
          [:div.span3
           (:attribute-id attribute)]]
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