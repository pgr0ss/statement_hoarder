(ns statement-hoarder.download
  (require [clojure.java.io :as io]
           [clojure.java.shell :as shell]
           [clojure.string :as string]
           [clj-webdriver.taxi :as taxi]))

(def TMP-PATH "/tmp/download/")

(defn convert-date [date-string]
  (let [[month day year] (string/split date-string #"/")]
    (string/join "-" [year month day])))

(defn clear-tmp []
  (shell/sh "rm" "-rf" TMP-PATH)
  (.mkdir (io/file TMP-PATH)))

(defn- exists? [filename]
  (let [file (io/file filename)]
    (and (.exists file)
         (> (.length file) 0))))

(defn- mv [from to]
  (when (exists? from)
    (shell/sh "mv" from to)))

(defn downloads-completed? []
  (let [all-downloads (.listFiles (io/file TMP-PATH))
        all-download-names (map str all-downloads)
        partial-downloads (filter (partial re-find #".part$") all-download-names)]
    (zero? (count partial-downloads))))

(defn download-with-function [statement-path folder original-filename final-filename link-function]
  (let [download-path (str TMP-PATH original-filename)
        final-path (str statement-path "/" folder "/" final-filename)]
    (.mkdir (io/file statement-path))
    (.mkdir (io/file (str statement-path "/" folder)))
    (if (exists? final-path)
      (do
        (println "  skipping" final-filename "since it already exists")
        false)
      (do
        (println "  downloading" final-filename)
        (link-function)
        (taxi/wait-until #(exists? download-path) 60000 500)
        (mv download-path final-path)
        true))))

(defn download-link [statement-path folder original-filename final-filename link]
  (download-with-function statement-path folder original-filename final-filename #(taxi/click link)))
