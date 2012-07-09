(ns statement-hoarder.sites.att
  (require [clj-webdriver.taxi :as taxi]
           [clojure.java.io :as io]
           [clojure.java.shell :as shell]
           [statement-hoarder.download :as download]
           [statement-hoarder.finders :as finders]))

(defn- att-statement-path [statement-path]
  (str statement-path "/AT&T"))

(defn- url [element]
  (.getAttribute (:webelement element) "href"))

(defn- download-pdf []
  (let [element (finders/find-link-by-text "My paper bill (PDF)")]
    (if (:webelement element)
      (taxi/click element)
      (do
        (taxi/click (finders/find-link-by-text "My Paper Bill"))
        (taxi/click (finders/find-link-by-text "Download PDF"))))))

(defn download [statement-path username password]
  (taxi/get-url "http://www.att.com")

  (taxi/input-text "#userid" username)
  (taxi/input-text "#password" password)

  (taxi/click (taxi/element "#tguardLoginButton"))
  (Thread/sleep 10000)

  (taxi/click (finders/find-link-by-text "Bill & Payments"))
  (taxi/click (finders/find-link-by-text "Billing history"))

  (let [statement-elements (taxi/elements "a[title=\"View\"]")]
    (doseq [url (doall (map url statement-elements))]
      (taxi/get-url url)
      (download-pdf)))

  (taxi/wait-until download/downloads-completed? 60000 500)

  (shell/sh "mkdir" "-p" (att-statement-path statement-path))

  (doseq [file (file-seq (io/file "/tmp/download"))]
    (when (.isFile file)
      (shell/sh "mv" (.getPath file) (att-statement-path statement-path)))))
