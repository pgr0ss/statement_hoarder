(ns statement-hoarder.core
  (require [clj-yaml.core :as yaml]
           [clj-webdriver.firefox :as firefox]
           [clj-webdriver.taxi :as taxi]
           [statement-hoarder.sites.blue-cross :as blue-cross]
           [statement-hoarder.sites.comed :as comed]
           [statement-hoarder.sites.rcn :as rcn]))

(defn- prompt [message]
  (let [console (System/console)
        value (.readPassword console message (to-array ""))]
    (String. value)))

(defn- password-prompt [site-name username]
  (prompt (str "Site: " (name site-name)
               "\n  Username: " username
               "\n  Password: ")))

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

(defn- site-config [site]
  (let [[site-name {:keys [username]}] site]
    {:site-name site-name
     :username username
     :password (password-prompt site-name username)}))

(defn- site-configs [config]
  (let [site-configs (map site-config (:sites config))]
    (doall site-configs)))

(defn -main [& args]
  (if-not (= 1 (count args))
    (usage)
    (let [config (config (first args))
          sites (site-configs config)]
      (taxi/set-driver! {:browser :firefox :profile (firefox/new-profile "firefox_profile")})
      (doseq [{:keys [site-name username password]} sites]
        (try
          (println "Downloading statements for" (name site-name))
          ((site-function site-name) username password)
          (catch Exception e (println "Caught exception: " (.getMessage e)))))
      (taxi/quit))))
