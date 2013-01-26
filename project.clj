(defproject nord "0.1.0-SNAPSHOT"
  :description "FIXME: write description"
  :url "http://example.com/FIXME"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [
                 [hiccup                                    "1.0.2"]
                 [liberator                                 "0.8.0"]
                 [org.clojure/clojure                       "1.4.0"]
                 [org.eclipse.jetty/jetty-util              "7.6.1.v20120215"]
                 [playnice                                  "0.0.6.11" :exclusions [ring/ring-jetty-adapter commons-codec]]
                 [ring                                      "1.1.6"]
                 [ring-json-params                          "0.1.3" :exclusions [org.codehaus.jackson/jackson-core-asl]]
                 [rotary "0.3.0"]
                 [org.clojure/data.csv "0.1.2"]
                 [clj-http "0.6.3"]]

  :source-paths ["src"]
  :test-paths ["test"]

  :plugins [[lein-ring "0.8.0"]
            [lein-pedantic "0.0.5"]
            [lein-beanstalk "0.2.6"]]

  :ring {:handler nord.core/app}
  
  :jvm-opts ["-Xms32M" "-Xmx128M" "-server"]
  :main nord.core
  :aws {:beanstalk
        {:environments [{:name "nord"
                         :cname-prefix "nord"}]}}
  )
