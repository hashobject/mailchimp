(ns mailchimp.core
  "Clojure wrapper for MailChimp API 2.0"
  (:require [clj-http.client :as client]
            [cheshire.core :as json]))


(defn- split-datacenter [api-key]
  (second (re-find #"[0-9a-zA-Z]+-([0-9a-zA-Z]+)" api-key)))

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
          output (json/parse-string (:body resp) true)]
      output)
  (catch Exception e
     (let [exception-info (.getData e)]
         (json/parse-string
             (get-in exception-info [:object :body]) true)))))

(defn call [api-key method data]
  (let [datacenter (split-datacenter api-key)]
    (post-request datacenter method (assoc data :apikey api-key))))

(defn lists->subscribe [api-key data]
  "Documentation for the 'data' paramerer located on
  http://apidocs.mailchimp.com/api/2.0/lists/subscribe.php"
  (call api-key "lists/subscribe" data))


(defn lists->unsubscribe [api-key data]
  "Documentation for the 'data' paramerer located on
  http://apidocs.mailchimp.com/api/2.0/lists/unsubscribe.php"
  (call api-key "lists/unsubscribe" data))

