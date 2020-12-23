(ns advent-of-code.core
  (:gen-class))
(require '[clojure.java.io :as io])
(require '[clojure.string :as str])

(def input (slurp (.getFile (io/resource "dec10_input.txt"))))

(defn parse-input
  [in]
  (map #(Integer/parseInt %) (str/split in #"\n")))


; sort the list (largest first), 
; then (because we're using the *entire* list), take the differences of all values
; then count the instances of 1 and instances of 3 
; (and add an extra 3 for the highest adapter)
; 

(defn find-differences
  "Find differences between pairs of elements"
  ([list] (find-differences list []))
  ([list all]
   (if (<= (count list) 1)
     all
     (find-differences (rest list) (conj all (- (first list) (second list)))))))

(defn solve
  ([] (solve (parse-input input)))
  ([list]
   (let [diffs (find-differences (sort > (conj list 0)))] ; add 0 to account for outlet
     (*
      (count (filter #(= 1 %) diffs))
      (inc (count (filter #(= 3 %) diffs)))))))

(defn -main
  "I don't do a whole lot ... yet."
  [& args]
  (println "Hello, World!"))
