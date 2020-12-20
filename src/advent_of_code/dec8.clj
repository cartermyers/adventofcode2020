(ns advent-of-code.dec8)
(require '[clojure.java.io :as io])
(require '[clojure.string :as str])

(def input (slurp (.getFile (io/resource "dec8_input.txt"))))

(defn parse-line
  [line]
  (let [s (str/split line #" ")]
    [(keyword (first s))
     (Integer/parseInt (second s))]))

(defn parse-input
  [in]
  (map parse-line (str/split in #"\n")))


; not as clean as I'd like, but it seems easy enough to read (just a lot of arguments)

(defn find-infinite-loop
  ([] (find-infinite-loop (parse-input input)))
  ([instructions] (find-infinite-loop instructions 0 0 #{}))
  ([instructions current accumulator already_executed]
   (if (contains? already_executed current)
     nil
     (if (= current (count instructions))
       accumulator
       (let [instruction (nth instructions current)
             next_already_executed (conj already_executed current)]
         (case (first instruction)
           :nop (find-infinite-loop instructions (inc current) accumulator next_already_executed)
           :acc (find-infinite-loop instructions (inc current) (+ accumulator (second instruction)) next_already_executed)
           :jmp (find-infinite-loop instructions (+ current (second instruction)) accumulator next_already_executed)))))))


; two parts needed to change:
; 1) perform a search to see if changing something works. I think the easiest way is to brute force it.
; 2) Changing to notice when the program terminates properly

(defn to-jmp
  [instruction]
  [:jmp (second instruction)])

(defn to-nop
  [instruction]
  [:nop (second instruction)])

(defn switch-and-see-if-completes
  [instructions instruction current]
  (case (first instruction)
    :nop (find-infinite-loop (assoc (vec instructions) current (to-jmp instruction)))
    :acc nil
    :jmp (find-infinite-loop (assoc (vec instructions) current (to-nop instruction)))))

(defn search-for-error
  "Goes through each instruction, and change it if needed"
  ([] (search-for-error (parse-input input)))
  ([instructions] (search-for-error instructions 0))
  ([instructions current]
   (let [res (switch-and-see-if-completes instructions (nth instructions current) current)]
     (if (nil? res)
       (search-for-error instructions (inc current))
       res))))