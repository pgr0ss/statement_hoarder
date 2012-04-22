(ns statement-hoarder.core
  (require [clj-webdriver.firefox :as firefox]
           [clj-webdriver.taxi :as taxi]
           [statement-hoarder.blue-cross :as blue-cross]
           [statement-hoarder.comed :as comed]
           [statement-hoarder.rcn :as rcn]))

(defn prompt [message]
  (let [console (System/console)
        value (.readPassword console message (to-array ""))]
    (String. value)))

(defn -main [& args]
  (let [blue-cross-user (prompt "Blue Cross User: ")
        blue-cross-password (prompt "Blue Cross Password: ")
        comed-user (prompt "ComEd User: ")
        comed-password (prompt "ComEd Password: ")
        rcn-user (prompt "RCN User: ")
        rcn-password (prompt "RCN Password: ")]
    (taxi/set-driver! {:browser :firefox :profile (firefox/new-profile "firefox_profile")})
    (blue-cross/download blue-cross-user blue-cross-password)
    (comed/download comed-user comed-password)
    (rcn/download rcn-user rcn-password)
    (taxi/quit)))
