(ns statement-hoarder.sites.blue-cross
  (require [clj-webdriver.taxi :as taxi]
           [clojure.java.shell :as shell]
           [clojure.string :as string]
           [statement-hoarder.download :as download]
           [statement-hoarder.finders :as finders]))

(def TABLE-SELECTOR "table#claim")

(defn- title-or-text [column]
  (let [webelement (:webelement column)
        title (.getAttribute webelement "title")
        text (.getText webelement)]
    (if-not (string/blank? title)
      title
      text)))

(defn download-eob [link]
  (taxi/click link)
  (let [eob-link (taxi/element "span.eob")]
    (when (:webelement eob-link)
      (taxi/click eob-link))
    (taxi/back)))

(defn download [statement-path username password]
  (taxi/get-url "https://members.hcsc.net/wps/portal/bam")

  (let [illinois-link (finders/find-link-by-text "BCBS Illinois")]
    (if (:webelement illinois-link)
      (taxi/click illinois-link)))

  (taxi/input-text "input[name=userName]" username)
  (taxi/input-text "input[name=password]" password)

  (taxi/click (taxi/element "input[type=submit]"))

  (taxi/click (finders/find-link-by-text "Claims Center"))

  (let [number-of-rows (count (taxi/elements (taxi/element TABLE-SELECTOR) "tr"))]
    (doseq [row-num (range 2 number-of-rows)]
      (let [table (taxi/element TABLE-SELECTOR)
            row (nth (taxi/elements table "tr") row-num)
            columns (taxi/elements row "td")
            visit-date (-> (nth columns 1) :webelement .getText)
            claim-type (-> (nth columns 3) :webelement .getText)
            formatted-visit-date (download/convert-date visit-date)
            provider (title-or-text (nth columns 5))
            formatted-provider (string/replace provider #" +" "_")
            total-charges (-> (nth columns 7) :webelement .getText)
            formatted-total-charges (string/replace (string/replace total-charges "$" "") "." "_")
            final-filename (str formatted-visit-date "_" formatted-provider "_" formatted-total-charges ".pdf")]
        (when-not (= claim-type "Prescription Drug")
          (download/download-with-function statement-path "Blue Cross" "eob1.pdf" final-filename (partial download-eob (nth columns 2))))))))
