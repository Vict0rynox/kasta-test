(ns kasta-test.core
  (:gen-class)
  (:require
    [kasta-test.filter :as filter]
    [compojure.core :refer :all]
    [compojure.handler :as handler]
    [cheshire.core :as json]
    ))


(defn json-response
  "Create response with serialize data to json"
  [data & [status]]
  {:status  (or status 200)
   :headers {"Content-Type" "application/hal+json; charset=utf-8"}
   :body    (json/generate-string data)})

(def api-routes
  (routes
    (POST "/filter" request
      (let [body-str (ring.util.request/body-string request)
            obj (json/parse-string body-str true)
            topic (:topic obj)
            filter (:q obj)]
        (if (or (nil? topic) (nil? filter))
          (json-response {:error (str "not setup `topic` or `q` -> `" obj "`")} 500)
          (json-response (filter/create topic filter))
          )))
    (GET "/filter" [id]
      (if (nil? id)
        (json-response (filter/filters-list))
        (try
          (->
            (Integer/parseInt id)
            (filter/filter-messages)
            (json-response))
          (catch ClassCastException _ (json-response {:error "not valid `id`"} 500)))))
    (DELETE "/filter" request
      (let [body-str (ring.util.request/body-string request)
            obj (json/parse-string body-str true)
            id (:id obj)]
        (if (or (nil? id) (not (number? id)))
          (json-response {:error "not setup `id`"} 500)
          (json-response (filter/to-delete id))))
      )
    ))

(def rest-api
  (handler/api api-routes))