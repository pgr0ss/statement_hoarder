(ns statement-hoarder.sites.rcn
  (require [clj-webdriver.firefox :as firefox]
           [clj-webdriver.taxi :as taxi]
           [clojure.java.shell :as shell]
           [clojure.java.io :as io]
           [statement-hoarder.download :as download]
           [statement-hoarder.finders :as finders]))

(defn- rcn-statement-path [statement-path]
  (str statement-path "/RCN"))

(defn- downloads-completed? []
  (let [all-downloads (.listFiles (io/file download/TMP-PATH))
        all-download-names (map str all-downloads)
        partial-downloads (filter (partial re-find #".part$") all-download-names)]
    (zero? (count partial-downloads))))

(defn download [statement-path username password]
  (taxi/get-url "https://my.rcn.com")

  (taxi/input-text "#username" username)
  (taxi/input-text "#password" password)

  (taxi/submit "#password")

  (taxi/get-url "https://my.rcn.com/billing/bills")

  (doseq [link (finders/find-links-by-text "Download")]
    (taxi/click link))

  (taxi/wait-until downloads-completed? 60000 500)

  (shell/sh "mkdir" "-p" (rcn-statement-path statement-path))

  (doseq [file (file-seq (io/file "/tmp/download"))]
    (when (.isFile file)
      (shell/sh "mv" (.getPath file) (rcn-statement-path statement-path)))))
