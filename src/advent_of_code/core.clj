(ns advent-of-code.core
  (:gen-class))
(require '[clojure.java.io :as io])
(require '[clojure.string :as str])
(require '[clojure.set])

(def input (slurp (.getFile (io/resource "dec4_input.txt"))))

(defn parse-passport
  "returns a map of all the fields present in the passport"
  [s]
  (into {} (map #(str/split % #":") (str/split s #"\s+"))))

(defn parse-input
  [s]
  (map parse-passport (str/split s #"\n\n")))

(def needed_fields #{"byr" "iyr" "eyr" "hgt" "hcl" "ecl" "pid"})

(defn valid-passport?
  ([passport]
   (valid-passport? passport needed_fields))
  ([passport fields]
   (clojure.set/subset? fields (set (map first passport)))))

(defn count-valid-passports
  ([passports]
   (count-valid-passports passports valid-passport?))
  ([passports f]
   (count (filter f passports))))

; TODO: have to validate number of digits and stuff too. sigh.
(defn valid-year?
  [passport key start end]
    (let [year (Integer. (get passport key))]
      (and (<= start year) (<= year end))))

(defn valid-birth?
  [passport]
  (valid-year? passport "byr" 1920 2002))

(defn valid-issue?
  [passport]
  (valid-year? passport "iyr" 2010 2020))

(defn valid-expiration?
  [passport]
  )

(defn valid-passport-2?
  [passport]
  (reduce #(and %1 %2) 
          (map #(% passport) [valid-birth?
                              ])))

(defn -main
  "I don't do a whole lot ... yet."
  [& args]
  (println "Hello, World!"))
