(ns statement-hoarder.sites.american-express
  (require [clj-webdriver.taxi :as taxi]
           [clojure.string :as string]
           [statement-hoarder.download :as download]
           [statement-hoarder.finders :as finders]))

(def TABLE-SELECTOR "div#ctl00_SPWebPartManager1_g_d4ac20d8_bb7c_4b89_a496_19eed73e874f table")

(defn download [statement-path username password]
  (taxi/get-url "https://www.americanexpress.com")

  (taxi/input-text "#UserID" username)
  (taxi/input-text "#Password" password)

  (taxi/click (taxi/element "#loginImage"))

  (taxi/click (finders/find-link-by-text "Statements & Activity"))

  (taxi/click (taxi/element "#LinkBilling"))

  (doseq [download-link (taxi/elements ".pdfImage")]
    (let [date-string (-> download-link :webelement .getText)
          [month day year] (string/split date-string #" ")
          filename (str "Statement_" month " " year ".pdf")]
      (download/download-link statement-path "American Express" filename filename download-link))))
