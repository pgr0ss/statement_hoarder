(ns statement-hoarder.core
  (require [clj-yaml.core :as yaml]
           [clj-webdriver.firefox :as firefox]
           [clj-webdriver.taxi :as taxi]
           [statement-hoarder.sites.blue-cross :as blue-cross]
           [statement-hoarder.sites.comed :as comed]
           [statement-hoarder.sites.rcn :as rcn]))

(defn prompt [message]
  (let [console (System/console)
        value (.readPassword console message (to-array ""))]
    (String. value)))

(defn- usage []
  (println "Usage: lein trampoline run <config.yml>")
  (System/exit 1))

(defn- config [config-file]
  (yaml/parse-string (slurp config-file)))

(defn- site-function [site]
  (case site
    :blue-cross blue-cross/download
    :comed comed/download
    :rcn rcn/download))

(defn -main [& args]
  (if-not (= 1 (count args))
    (usage)
    (let [config (config (first args))]
      (taxi/set-driver! {:browser :firefox :profile (firefox/new-profile "firefox_profile")})
      (doseq [[site {:keys [username]}] (:sites config)]
        (println "Downloading statements for" (name site))
        (let [site-function (site-function site)
              password (prompt (str "  Username: " username "\n  Password: "))]
          (try
            (site-function username password)
            (catch Exception e (println "Caught exception: " (.getMessage e))))))
      (taxi/quit))))
