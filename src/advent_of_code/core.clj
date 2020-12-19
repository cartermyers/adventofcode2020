(ns advent-of-code.core
  (:gen-class))
(require '[clojure.java.io :as io])
(require '[clojure.string :as str])

(def input (slurp (.getFile (io/resource "dec7_input.txt"))))

; a bit messy, but this parsing code looks to get a map of maps:
; 
; {:light-red {:bright-white 1, :muted-yellow 2}}



(defn create-bag-keyword
  "Given a string like
   1 bright white bag -> :bright-white
   or 
   2 muted yellow bags -> :muted-yellow"
  [bag]
  (keyword (str/join "-" (rest (str/split (first (str/split bag #" bag")) #" ")))))

(defn parse-bag
  "Returns a pair of bag and entries (to be used in a map)"
  [bag]
  [(create-bag-keyword bag)
   (Integer/parseInt (first (str/split bag #" ")))])

(defn parse-contains
  [bags]
  (if (str/includes? bags "no other")
    {}
    (into {} (map parse-bag (str/split bags #", ")))))

(defn parse-rule
  "Returns pair, the first entry being the bag at the beginning, the second being a map of what it can contain"
  [rule]
  (let [s (str/split rule #" bags contain ")]
    [(keyword (str/replace (first s) #" " "-")) 
     (parse-contains (second s))]))

(defn parse-input
  "Returns a map of maps. Each bag is a key, and it's entry contains a map of what bags it can contain"
  [in]
  (into {} (map parse-rule (str/split in #"\n"))))


; now time to look for the shiny gold bag holders
; 

(defn can-hold-shiny-bag?
  [all-rules bags] 
  (if (empty? bags)
    false
    (if (not (nil? (bags :shiny-gold)))
      true
      (some #(can-hold-shiny-bag? all-rules (get all-rules (first %))) bags))))

(def can-hold-shiny-bag?-memo (memoize can-hold-shiny-bag?))

(defn count-all
  ([] (count-all (parse-input input)))
  ([rules]
   (count (filter #(can-hold-shiny-bag?-memo rules (get rules (first %))) rules))))

(defn count-bags
  [bags]
  (reduce + (map second bags)))


(defn count-bags-in-bag
  ([] (count-bags-in-bag (parse-input input)))
  ([rules] (count-bags-in-bag rules :shiny-gold))
  ([rules bag]
   (+ (count-bags (get rules bag))
      (reduce + (map #(* (second %) (count-bags-in-bag rules (first %))) (get rules bag))))
   
   
   ))


(defn -main
  "I don't do a whole lot ... yet."
  [& args]
  (println "Hello, World!"))
