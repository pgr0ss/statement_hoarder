(ns statement-hoarder.comed
  (require [clj-webdriver.firefox :as firefox]
           [clj-webdriver.taxi :as taxi]
           [clojure.java.shell :as shell]
           [clojure.java.io :as io]
           [clojure.string :as string]))

(defn download [username password]
  (shell/sh "mkdir" "-p" "statements/ComEd")

  (taxi/get-url "https://www.comed.com")

  (taxi/input-text "#ctl00_login_txtUserName" username)
  (taxi/input-text "#ctl00_login_txtPassword" password)

  (taxi/click (taxi/element "#ctl00_login_btnLogin"))

  (taxi/get-url "https://www.comed.com/MyAccount/Residential/my-bill/download-estatements/Pages/default.aspx")

  (def table (taxi/element "div#ctl00_SPWebPartManager1_g_d4ac20d8_bb7c_4b89_a496_19eed73e874f table"))

  (let [number-of-rows (count (taxi/elements table "tr"))]
    (doseq [row-num (range 1 number-of-rows)]
      (Thread/sleep 5000)
      (let [table (taxi/element "div#ctl00_SPWebPartManager1_g_d4ac20d8_bb7c_4b89_a496_19eed73e874f table")
            row (nth (taxi/elements table "tr") row-num)
            columns (taxi/elements row "td")
            bill-date (-> columns first :webelement .getText)
            [bill-month bill-day bill-year] (string/split bill-date #"/")
            formatted-bill-date (string/join "-" [bill-year bill-month bill-day])
            link (taxi/element (last columns) "a")]
        (taxi/click link)
        (shell/sh "mv" "/tmp/download/default.aspx" (str "statements/ComEd/" formatted-bill-date ".pdf"))))))
