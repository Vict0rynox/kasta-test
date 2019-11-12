(ns kasta-test.filter
  (:require [clojure.tools.logging :as log])
  (:import (org.apache.kafka.common.serialization StringDeserializer)
           (org.apache.kafka.clients.consumer KafkaConsumer ConsumerRecord)
           (java.util.regex Pattern))
  )

(def bootstrap-server "localhost:9092")

;;FIXME: add validator
(def filters (agent {}))
(def max-id (ref 0))

(defn- gen-filter-id
  "Generate id for filter"
  []
  (dosync (alter max-id inc)))

(defn- build-consumer
  "Create consumer instance to consume form the provider kafka topic name"
  [consumer-topic bootstrap-server filter-id]
  (let [consumer-props
        {"bootstrap.servers",  bootstrap-server
         "group.id",           (str consumer-topic "-" filter-id)
         "key.deserializer",   StringDeserializer
         "value.deserializer", StringDeserializer
         "enable.auto.commit", "true"}]
    (doto (KafkaConsumer. consumer-props)
      (.subscribe [consumer-topic]))))



(defn- add-message
  "add message to filter"
  [filter-id message]
  (send filters update-in [filter-id :messages] conj message))

(defn- to-delete?
  "Check if filter mark to deleted"
  [filter-id]
  (let [to-delete (:to_delete (@filters filter-id))]
    to-delete))

(defn- delete
  "Close consumer and delete filter"
  [filter-id]
  (let [filter (@filters filter-id)
        consumer (:consumer filter)]
    (.close consumer)
    (send filters dissoc filter-id)))


;;FIXME: maybe way with KSQL and Stream - better and more `kafka way`...
(defn- filter-handler [filter-id consumer filter]
  (fn []
    (while (not (to-delete? filter-id))
      (let [records (.poll consumer 100)]
        (doseq [^ConsumerRecord record records]
          (log/info "Sending on value" (str "Processed Value: " (.value record)))
          (if (->
                (str "(?i)" (Pattern/quote filter))
                (re-pattern)
                (re-find (.value record))
                (nil?)
                (not))
            (add-message filter-id (.value record))
            (log/info "Value `" (.value record) "`, not path filter.")))
        (.commitAsync consumer)))
    (delete filter-id)))


(defn- add-filter
  "add new filter"
  [filter-id {topic :topic filter :filter consumer :consumer}]
  (send filters assoc filter-id {:consumer  consumer
                                 :filter    filter
                                 :topic     topic
                                 :messages  ()
                                 :to_delete false
                                 }))


;; Public client API

(defn has-filter?
  "check filter exists"
  ([filter-id] (not (nil? (@filters filter-id))))
  ([topic q]
   (not (empty? (filter #(let [filter (select-keys %1 [:filter :topic])]
                           (and (= topic (:topic filter)) (= q (:filter filter))))
                        (vals @filters))))))

(defn filters-list
  "return filters list"
  []
  (or (map (fn [[id val]]
             {:id     id
              :topic  (:topic val)
              :filter (:filter val)
              }) @filters) (list)))

(defn create
  "create filter. Add filter and start consumer."
  [input-topic q]
  (if (has-filter? input-topic q)
    nil
    (let [filter-id (gen-filter-id)
          consumer (build-consumer input-topic bootstrap-server q)
          filter {
                  :consumer consumer
                  :filter   q
                  :topic    input-topic}
          ]
      (add-filter filter-id filter)
      (future ((filter-handler filter-id consumer q)))
      filter-id)))

(defn to-delete
  "Mark filter to delete"
  [filter-id]
  (if (has-filter? filter-id)
    (do
      (send filters update-in [filter-id :to_delete] (fn [_] true))
      filter-id)
    nil))

(defn filter-messages
  "receive filter messages"
  [filter-id]
  (let [filter (@filters filter-id)
        messages (if (nil? filter) nil (:messages filter))]
    messages))

