(defproject nord "0.1.0-SNAPSHOT"
  :description "FIXME: write description"
  :url "http://example.com/FIXME"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [
                 [hiccup                       "1.0.5"]
                 [liberator                    "0.11.0"]
                 [org.clojure/clojure          "1.5.1"]
                 [playnice                     "0.0.6.11"
                  :exclusions [ring/ring-jetty-adapter]]
                 [ring                         "1.2.1"]
                 [org.clojure/data.csv         "0.1.2"]
                 [clj-http                     "0.6.3"]
                 [org.clojure/java.jdbc        "0.3.3"]
                 [cheshire                     "5.3.1"]
                 [postgresql/postgresql        "9.1-901.jdbc4"]]

  :min-lein-version "2.0.0"

  :source-paths ["src"]
  :test-paths ["test"]

  :uberjar-name "nord.jar"

  :profiles {:dev
             {:main nord.core/-dev-main}}

  :main nord.core)
