(ns statement-hoarder.finders
  (require [clj-webdriver.core :as core]
           [clj-webdriver.taxi :as taxi]))

(defn find-links-by-text [text]
  (core/find-elements-by taxi/*driver* (core/by-link-text text)))

(defn find-link-by-text [text]
  (first (find-links-by-text text)))
