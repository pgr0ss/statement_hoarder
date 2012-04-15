(ns statement-hoarder.rcn
  (require [clj-webdriver.firefox :as firefox]
           [clj-webdriver.taxi :as taxi]
           [clojure.java.shell :as shell]
           [clojure.java.io :as io]))

(defn download [username password]
  (taxi/get-url "https://my.rcn.com")

  (taxi/input-text "#username" username)
  (taxi/input-text "#password" password)

  (taxi/submit "#password")

  (taxi/get-url "https://my.rcn.com/billing/bills")

  (def download-links (filter #(= "Download" (.getText (:webelement %))) (taxi/elements "a")))

  (doseq [link download-links]
    (taxi/click link))

  (shell/sh "mkdir" "-p" "statements/RCN")

  (doseq [file (file-seq (io/file "/tmp/download"))]
    (when (.isFile file)
      (shell/sh "mv" (.getPath file) "statements/RCN"))))
