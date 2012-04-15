(ns statement-hoarder.core
  (require [clj-webdriver.firefox :as firefox]
           [clj-webdriver.taxi :as taxi]
           [statement-hoarder.rcn :as rcn]))

(defn prompt [message]
  (let [console (System/console)
        value (.readPassword console message (to-array ""))]
    (String. value)))

(defn -main [& args]
  (let [rcn-user (prompt "RCN User: ")
        rcn-password (prompt "RCN Password: ")]
  (taxi/set-driver! {:browser :firefox :profile (firefox/new-profile "firefox_profile")})
  (rcn/download rcn-user rcn-password)
  (taxi/quit)))
