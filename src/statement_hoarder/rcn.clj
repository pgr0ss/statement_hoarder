(ns statement-hoarder.rcn
  (require [clj-webdriver.firefox :as firefox]
           [clj-webdriver.taxi :as taxi]))

(defn download []
  (taxi/get-url "https://my.rcn.com")

  (taxi/input-text "#username" "username")
  (taxi/input-text "#password" "password")

  (taxi/submit "#password")

  (taxi/get-url "https://my.rcn.com/billing/bills")

  (def download-links (filter #(= "Download" (.getText (:webelement %))) (taxi/elements "a")))

  (doseq [link download-links]
    (taxi/click link)))
