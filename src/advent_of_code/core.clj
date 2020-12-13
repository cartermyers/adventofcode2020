(ns advent-of-code.core
  (:gen-class))
(require '[clojure.java.io :as io])
(require '[clojure.string :as str])
(require '[clojure.set])

(def input (slurp (.getFile (io/resource "dec4_input.txt"))))

(defn parse-passport-key-fields
  "returns all the key fields present in the passport"
  [s]
  (map first (map #(str/split % #":") (str/split s #"\s+"))))

(defn parse-input
  [s]
  (map parse-passport-key-fields (str/split s #"\n\n")))

(def needed_fields #{"byr" "iyr" "eyr" "hgt" "hcl" "ecl" "pid"})

(defn valid-passport?
  ([passport_fields]
   (valid-passport? passport_fields needed_fields))
  ([passport_fields fields]
   (clojure.set/subset? fields (set passport_fields))))

(defn count-valid-passports
  [passports]
  (count (filter valid-passport? passports)))


(defn -main
  "I don't do a whole lot ... yet."
  [& args]
  (println "Hello, World!"))
