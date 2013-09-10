(ns mailchimp.core
  "Clojure wrapper for MailChimp API 2.0"
  (:require [clj-http.client :as client]
            [cheshire.core :as json]))


(defn make-url [datacenter route]
  (str "https://" datacenter ".api.mailchimp.com/2.0/" route))


(defn post-request
  "Make a generic POST HTTP request"
  [datacenter route body]
  (try
    (let [url (make-url datacenter route)
          json (json/generate-string body)
          resp (client/post url
                {:accept :json
                 :content-type :json
                 :body json})
          output (json/parse-string (:body resp))]
      output)
  (catch Exception e
     (let [exception-info (.getData e)]
     (select-keys
       (into {} (map (fn [[k v]] [(keyword k) v])
         (json/parse-string
             (get-in exception-info [:object :body]))))
             (vector :status :message :code))))))


(defn lists->subscribe [api-key datacenter data]
  "Documentation for the 'data' paramerer located on
  http://apidocs.mailchimp.com/api/2.0/lists/subscribe.php"
  (post-request datacenter "lists/subscribe" (assoc data :apikey api-key)))


(defn lists->unsubscribe [api-key datacenter data]
  "Documentation for the 'data' paramerer located on
  http://apidocs.mailchimp.com/api/2.0/lists/unsubscribe.php"
  (post-request datacenter "lists/unsubscribe" (assoc data :apikey api-key)))
