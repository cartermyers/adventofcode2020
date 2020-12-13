(ns advent-of-code.dec3)
(require '[clojure.java.io :as io])
(require '[clojure.string :as str])

(def input (slurp (.getFile (io/resource "dec3_input.txt"))))

; parse the input

(defn parse-line
  "Returns a list of bool"
  [line]
  (map #(= \# %) line))

(defn parse-input
  "Given a string, delimited by endline, returns a list of list of bool.
   First index is for y direction, second index is x direction."
  [s]
  (map parse-line (str/split s #"\n")))


; do some math to see how many tree's we'll hit
; original problem uses slope going right 3 and down 1.

(defn hits-tree?
  [line x-pos]
  (nth line (mod x-pos (count line))))

(defn count-trees-hit-y-direction
  "The map infinitely repeats to the right, so we'll use the number in y as the limit"
  [trees y-slope]
  (count (filter identity (map-indexed #(hits-tree? %2 (* y-slope %1)) trees))))

; part 2
; 
; just try to hack an answer using the last part

(def slopes [[1 1] [3 1] [5 1] [7 1] [1 2]]) ; `(y x)

(defn count-trees-hit
  [trees slope]
  (count-trees-hit-y-direction (take-nth (second slope) trees) (first slope)))

(defn multiply-all-slopes
  ([trees s]
   (reduce * (map #(count-trees-hit trees %) s)))
  ([trees]
   (multiply-all-slopes trees slopes)))
