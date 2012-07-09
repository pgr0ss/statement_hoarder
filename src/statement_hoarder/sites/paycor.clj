(ns statement-hoarder.sites.paycor
  (require [clj-webdriver.taxi :as taxi]
           [clojure.java.io :as io]
           [clojure.java.shell :as shell]
           [clojure.string :as string]
           [statement-hoarder.download :as download]
           [statement-hoarder.finders :as finders]))

(def PREVIOUS-SELECTOR "#ctl00_ctl00_placeHolderMain_contentPHMain_DisplayPdf1_roiPrevious")

(defn- paycor-statement-path [statement-path]
  (str statement-path "/Paycor"))

(defn download [statement-path username password]
  (taxi/get-url "http://www.paycor.com")

  (taxi/click (taxi/element "#sign_in_button"))

  (finders/wait-until-exists "#ctl00_ctl00_placeHolderMain_paMain_txtLoginName")

  (taxi/input-text "#ctl00_ctl00_placeHolderMain_paMain_txtLoginName" username)
  (taxi/input-text "#ctl00_ctl00_placeHolderMain_paMain_txtPassword" password)

  (taxi/click (taxi/element "#ctl00_ctl00_placeHolderMain_paMain_btnLoginAjax"))

  (Thread/sleep 5000)

  (taxi/click (taxi/element "#ctl00_ctl00_placeHolderMain_contentPHMain_DisplayEEMain1_roiChecks"))

  (while (taxi/exists? PREVIOUS-SELECTOR)
    (taxi/click (taxi/element PREVIOUS-SELECTOR))
    (Thread/sleep 1000))

  (shell/sh "mkdir" "-p" (paycor-statement-path statement-path))

  (doseq [file (file-seq (io/file "/tmp/download"))]
    (when (.isFile file)
      (shell/sh "mv" (.getPath file) (paycor-statement-path statement-path)))))
