(ns statement-hoarder.core
  (require [clj-webdriver.firefox :as firefox]
           [clj-webdriver.taxi :as taxi]
           [statement-hoarder.rcn :as rcn]))

(defn -main [& args]
  (taxi/set-driver! {:browser :firefox :profile (firefox/new-profile "firefox_profile")})
  (rcn/download)
  (taxi/quit))
