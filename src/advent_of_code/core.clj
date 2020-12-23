(ns advent-of-code.core
  (:gen-class))
(require '[clojure.java.io :as io])
(require '[clojure.string :as str])

(def input (slurp (.getFile (io/resource "dec9_input.txt"))))

(defn parse-input
  [in]
  (map #(Long/valueOf %) (str/split in #"\n")))


; this problem is similar to what happened in dec1 part 2, so steal some code from there:
(defn get-pair
  ; finds the first number that adds to first to sum
  [f sum list]
  (first (filter #(= sum (+ % f)) list)))

(defn find-2
  ([list sum] (find-2 (first list) sum (rest list)))
  ([current sum list]
   (if (empty? list)
     nil
     (if-let [pair (get-pair current sum list)]
       pair
       (find-2 (first list) sum (rest list))))))

; now for new code:
(defn find-weakness
  ([] (find-weakness (parse-input input) 25))
  ([list preamble_length]
   (let [current (nth list preamble_length)]
     (if (nil? (find-2 (take preamble_length list) current))
       current
       (find-weakness (rest list) preamble_length)))))


; take at least 2 numbers, see if them sum
; keep taking more numbers until it's over the sum
; drop the first number and start over 
; this was logically fine, but takes too long. leads to stack overflow
; 
; a better option might be to do more of an index based option
; as in, [start, stop) -- starting at [0, 1). 
; If apply + (subvec v start stop) = sum, then done
; else if greater than sum, then increment start
; else increment stop
; 

(defn find-contiguous-set
  ([vector sum] (find-contiguous-set vector sum 0 1))
  ([vector sum start stop]
   (let [l (subvec vector start stop)]
     (if (= sum (apply + l))
       l
       (if (> (apply + l) sum)
         (find-contiguous-set vector sum (inc start) stop)
         (find-contiguous-set vector sum start (inc stop))
         )))))

(defn solve
  ([] (solve (parse-input input) 25))
  ([list preamble_length]
   (let [l (find-contiguous-set (vec list) (find-weakness list preamble_length))]
     (+ (apply min l) (apply max l)))))


(defn -main
  "I don't do a whole lot ... yet."
  [& args]
  (println "Hello, World!"))
