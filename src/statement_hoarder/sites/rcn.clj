(ns statement-hoarder.sites.rcn
  (require [clj-webdriver.firefox :as firefox]
           [clj-webdriver.taxi :as taxi]
           [clojure.java.shell :as shell]
           [clojure.java.io :as io]
           [statement-hoarder.finders :as finders]))

(defn download [username password]
  (taxi/get-url "https://my.rcn.com")

  (taxi/input-text "#username" username)
  (taxi/input-text "#password" password)

  (taxi/submit "#password")

  (taxi/get-url "https://my.rcn.com/billing/bills")

  (doseq [link (finders/find-links-by-text "Download")]
    (taxi/click link))

  (shell/sh "mkdir" "-p" "statements/RCN")

  (doseq [file (file-seq (io/file "/tmp/download"))]
    (when (.isFile file)
      (shell/sh "mv" (.getPath file) "statements/RCN"))))
