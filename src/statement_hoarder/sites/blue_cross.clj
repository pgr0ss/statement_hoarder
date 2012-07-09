(ns statement-hoarder.sites.blue-cross
  (require [clj-webdriver.taxi :as taxi]
           [clojure.java.shell :as shell]
           [clojure.string :as string]
           [statement-hoarder.download :as download]
           [statement-hoarder.finders :as finders]))

(def TABLE-SELECTOR "table#claims")

(defn download [statement-path username password]
  (taxi/get-url "https://members.hcsc.net/wps/portal/bam")

  (let [illinois-link (finders/find-link-by-text "BCBS Illinois")]
    (if (:webelement illinois-link)
      (taxi/click illinois-link)))

  (taxi/input-text "input[name=userName]" username)
  (taxi/input-text "input[name=password]" password)

  (taxi/click (taxi/element "input[alt=\"Login\"]"))

  (taxi/click (finders/find-link-by-text "Visits & Claims"))

  (let [number-of-rows (count (taxi/elements (taxi/element TABLE-SELECTOR) "tr"))]
    (doseq [row-num (range 1 number-of-rows)]
      (let [table (taxi/element TABLE-SELECTOR)
            row (nth (taxi/elements table "tr") row-num)
            columns (taxi/elements row "td")
            visit-date (-> columns first :webelement .getText)
            formatted-visit-date (download/convert-date visit-date)
            provider (-> (nth columns 2) :webelement .getText)
            formatted-provider (string/replace provider " " "_")
            total-charges (-> (nth columns 4) :webelement .getText)
            formatted-total-charges (string/replace (string/replace total-charges "$" "") "." "_")
            final-filename (str formatted-visit-date "_" formatted-provider "_" formatted-total-charges ".pdf")
            link (taxi/element (last columns) "a")]
        (if (and (:webelement link)
                 (download/download statement-path link "eob1.pdf" final-filename "Blue Cross"))
          (taxi/back))))))
