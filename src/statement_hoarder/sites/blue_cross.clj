(ns statement-hoarder.sites.blue-cross
  (require [clj-webdriver.taxi :as taxi]
           [clojure.java.io :as io]
           [clojure.java.shell :as shell]
           [clojure.string :as string]
           [statement-hoarder.download :as download]
           [statement-hoarder.finders :as finders]))

(def TABLE-SELECTOR "table#claims")

(defn download [statement-path username password]
  (taxi/get-url "https://www.bcbsil.com")

  (taxi/click (taxi/element "#loginBtn"))
  (taxi/wait-until #(taxi/exists? "div.logOpen"))

  (taxi/input-text "#userId" username)
  (taxi/input-text "#pswd" password)

  (taxi/click (taxi/element "input[src=\"/images/login_button.jpg\"]"))

  (taxi/click (first (finders/find-links-by-text "Visits & Claims")))

  (let [number-of-rows (count (taxi/elements (taxi/element TABLE-SELECTOR) "tr"))]
    (doseq [row-num (range 1 number-of-rows)]
      (let [table (taxi/element TABLE-SELECTOR)
            row (nth (taxi/elements table "tr") row-num)
            columns (taxi/elements row "td")
            visit-date (-> columns first :webelement .getText)
            formatted-visit-date (download/convert-date visit-date)
            provider (-> (nth columns 2) :webelement .getText)
            formatted-provider (string/replace provider " " "_")
            final-filename (str formatted-visit-date "_" formatted-provider ".pdf")
            link (taxi/element (last columns) "a")]
        (download/download statement-path link "eob1.pdf" final-filename "BlueCross")
        (taxi/back)))))
