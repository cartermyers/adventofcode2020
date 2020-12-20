(ns advent-of-code.core
  (:gen-class))
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
     accumulator
     (let [instruction (nth instructions current)
           next_already_executed (conj already_executed current)]
       (case (first instruction)
         :nop (find-infinite-loop instructions (inc current) accumulator next_already_executed)
         :acc (find-infinite-loop instructions (inc current) (+ accumulator (second instruction)) next_already_executed)
         :jmp (find-infinite-loop instructions (+ current (second instruction)) accumulator next_already_executed))))))


(defn -main
  "I don't do a whole lot ... yet."
  [& args]
  (println "Hello, World!"))
