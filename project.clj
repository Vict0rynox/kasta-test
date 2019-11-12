(defproject kasta-test "0.1.0-SNAPSHOT"
  :description "FIXME: write description"
  :url "http://example.com/FIXME"
  :license {:name "EPL-2.0 OR GPL-2.0-or-later WITH Classpath-exception-2.0"
            :url  "https://www.eclipse.org/legal/epl-2.0/"}
  :dependencies [
                 [org.clojure/clojure "1.10.0"]
                 [org.clojure/tools.logging "0.4.0"]
                 [org.slf4j/log4j-over-slf4j "1.7.25"]
                 [ch.qos.logback/logback-classic "1.2.3"]

                 [org.apache.kafka/kafka-streams "2.2.0"]
                 [org.apache.kafka/kafka-clients "2.2.0"]
                 [org.apache.curator/curator-test "2.8.0"]

                 ;;[me.raynes/fs "1.4.6"]

                 [ring/ring-core "1.7.1"]
                 [compojure "1.6.1"]
                 [cheshire "5.9.0"]
                 ]
  :plugins [[lein-ring "0.12.5"]]
  :ring {:handler kasta-test.core/rest-api}
  ;;:main ^:skip-aot kasta-test.core
  :repl-options {:init-ns kasta-test.core})
