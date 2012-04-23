(ns statement-hoarder.comed
  (require [clj-webdriver.taxi :as taxi]
           [clojure.java.io :as io]
           [clojure.java.shell :as shell]
           [clojure.string :as string]
           [statement-hoarder.download :as download]))

(def TABLE-SELECTOR "div#ctl00_SPWebPartManager1_g_d4ac20d8_bb7c_4b89_a496_19eed73e874f table")

(defn download [username password]
  (taxi/get-url "https://www.comed.com")

  (taxi/input-text "#ctl00_login_txtUserName" username)
  (taxi/input-text "#ctl00_login_txtPassword" password)

  (taxi/click (taxi/element "#ctl00_login_btnLogin"))

  (taxi/get-url "https://www.comed.com/MyAccount/Residential/my-bill/download-estatements/Pages/default.aspx")

  (let [number-of-rows (count (taxi/elements (taxi/element TABLE-SELECTOR) "tr"))]
    (doseq [row-num (range 1 number-of-rows)]
      (taxi/wait-until #(taxi/exists? TABLE-SELECTOR))
      (let [table (taxi/element TABLE-SELECTOR)
            row (nth (taxi/elements table "tr") row-num)
            columns (taxi/elements row "td")
            bill-date (-> columns first :webelement .getText)
            final-filename (str (download/convert-date bill-date) ".pdf")
            link (taxi/element (last columns) "a")]
        (download/download link "default.aspx" final-filename "ComEd")))))
