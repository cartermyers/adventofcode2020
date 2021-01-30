(ns advent-of-code.core
  (:gen-class))
(require '[clojure.string :as str])
(require '[clojure.java.io :as io])

(def input (slurp (.getFile (io/resource "dec19_input.txt"))))

(defn parse-single-dependant
  [dependant]
  ; assume only literals are "a" or "b"
  (map #(if (or (= "\"a\"" %) (= "\"b\"" %)) (second %) (Integer/parseInt %))
       (str/split dependant #" ")))

(defn parse-dependents
  [dependents]
  (map parse-single-dependant (str/split dependents #" \| ")))

(defn parse-rule
  [rule]
  (let [a (str/split rule #": ")]
    [(Integer/parseInt (first a))
     (parse-dependents (second a))]))

(defn parse-rules
  [rules]
  (into {} (map parse-rule (str/split rules #"\n"))))

(defn parse-messages
  [messages]
  (str/split messages #"\n"))

(defn parse-input
  [in]
  (let [a (str/split in #"\n\n")]
    {:rules (parse-rules (first a))
     :messages (parse-messages (second a))}))

; end of parsing code
; 

; 1. for each list of dependents,
;     1a. if dependents are a char, return a string of char
;     1b. need to replace for each in list
;     this is done by going to 1 for each. 
;     
; I want it to look something like this.
; Given:
; 0: 1 2
; 1: "a"
; 2: 1 3 | 3 1
; 3: "b"
; 
; return '(("a") ("ab" "ba"))
; by going like 0 -> 1 -> '(a)
; then 2 -> 1 -> '(a) -> 3 -> '(ab)
; -> 3 -> '(b) -> 1 -> '(ba)   -> '('(ab) '(ba))
; 

(defn is-char?
  [c]
  (or (= '\a c) (= \b c)))

(defn expand-rules
  ([rules] (expand-rules rules 0))
  ([rules number]
   (let [all-dependents (get rules number)]
     (if (is-char? (first (first all-dependents)))
       (first (first all-dependents))
       (map (fn [dependents]
              (map #(expand-rules rules %) dependents))
            all-dependents)))))

; I basically need to make decisions about when to be OR
; 
; 1. For each possibility:
;     Go over each element
;     1a. If it's a character, append to all results
;     1b. If it's a list, go to 1 (to get a list of other possibilities)
;     and then append to all results
;     
; The basic case for this is '( (\a) (\b) ) right?
; Should return '("a" "b")
; And '(\a) should return ("a")
; So getting the comination of ("a") to ("a" "b") should give
; ("aa" "ab")


(declare combine)

(defn add-to-results
  [results new-results]
  (println "C: " results new-results)
  (into #{} (for [r results x (map str new-results)] (str r x))))

(defn combine-helper
  "Takes in a list (possibly nested) that represents a possibility."
  [possibility results]
  (println "B: " results possibility)
  (if (empty? possibility)
    results
    (combine-helper (rest possibility)
                    (add-to-results results
                                    (if (is-char? (first possibility))
                                      [(first possibility)]
                                      (combine (first possibility)))))
    ))

(defn combine
  [possibilities] 
  (reduce #(into #{} (combine-helper %2 %1)) #{""} possibilities))



(defn -main
  "I don't do a whole lot ... yet."
  [& args]
  (println "Hello, World!"))
