(ns statement-hoarder.download
  (require [clojure.java.io :as io]
           [clojure.string :as string]
           [clj-webdriver.taxi :as taxi]))

(defn convert-date [date-string]
  (let [[month day year] (string/split date-string #"/")]
    (string/join "-" [year month day])))

(defn- exists? [filename]
  (let [file (io/file filename)]
    (and (.exists file)
         (> (.length file) 0))))

(defn- mv [from to]
  (when (exists? from)
    (.renameTo (io/file from) (io/file to))))

(defn download [link original-filename final-filename folder]
  (let [download-path (str "/tmp/download/" original-filename)
        final-path (str "statements/" folder "/" final-filename)]
    (taxi/click link)
    (taxi/wait-until #(exists? download-path))
    (println download-path final-path)
    (mv download-path final-path)))
