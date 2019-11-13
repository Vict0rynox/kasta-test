(ns kasta-test.core
  (:gen-class)
  (:require
   [kasta-test.filter :as filter]
   [compojure.core :refer :all]
   [ring.middleware.defaults :as default-middleware]
   [ring.middleware.json :as json-middleware]))


(defn- warp-data-to-resp [data] {:body data})

(defn- as-int
  [val]
  (try
    (Integer/parseInt val)
    (catch NumberFormatException e val)))

;;handlers

(defn- create-filter [{topic :topic, filter :q}] (hash-map :id (filter/create topic filter)))

(defn- select-filters [] (prn-str (filter/filters-list)))

(defn- select-filter [id] (or (filter/messages-by-id id) (list)))

;;FIXME: maybe need change params to `id`
(defn- delete-filter [id] (hash-map :id (filter/to-delete id)))

;; routes
(def api-routes
  (routes
   (POST "/filter" {body :body} (warp-data-to-resp (create-filter body)))
   (GET "/filter" [id :<< as-int] (warp-data-to-resp (if (nil? id) (select-filters) (select-filter id))))
   (DELETE "/filter" {{id :id} :body} (warp-data-to-resp (delete-filter id)))))

;;handler (booting point)
(def rest-api
  (->
   api-routes
   (json-middleware/wrap-json-body {:keywords? true})
   (default-middleware/wrap-defaults default-middleware/api-defaults)
   (json-middleware/wrap-json-response)))