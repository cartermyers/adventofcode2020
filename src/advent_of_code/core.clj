(ns advent-of-code.core
  (:gen-class))
(require '[clojure.java.io :as io])
(require '[clojure.string :as str])

(def input (slurp (.getFile (io/resource "dec9_input.txt"))))

(defn parse-input
  [in]
  (map #(Integer/parseInt %) (str/split in #"\n")))


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

(defn find-contiguous-set
  ([list sum] (find-contiguous-set list sum 2))
  ([list sum length]
   (print length \newline)
   (let [l (take length list)]
     (if (= sum (apply + l))
       l
       (if (> (apply + l) sum)
         (find-contiguous-set (rest list) sum)
         (find-contiguous-set list sum (inc length))
         )))))

(defn solve
  ([] (solve (parse-input input) 25))
  ([list preamble_length]
   (let [l (find-contiguous-set list (find-weakness list preamble_length))]
     (+ (apply min l) (apply max l)))))


(defn -main
  "I don't do a whole lot ... yet."
  [& args]
  (println "Hello, World!"))
