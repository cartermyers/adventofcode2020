(ns advent-of-code.dec13)
(require '[clojure.string :as str])
(require '[clojure.java.io :as io])

(def input (slurp (.getFile (io/resource "dec13_input.txt"))))

(defn parse-ids-part-1
  [ids]
  (map #(Integer/parseInt %) (str/split ids #",")))

(defn parse-input-part-1
  [in]
  (let [a (str/split in #"\n")]
    {:earliest-depart (Integer/parseInt (first a))
     :ids (parse-ids-part-1 (second a))}))

(defn parse-id-part-2
  [index id]
  [index (if (= "x" id) :x (Integer/parseInt id))])

(defn parse-input-part-2
  [in]
  (filter #(not= :x (second %)) (map-indexed parse-id-part-2 (str/split (second (str/split in #"\n")) #","))))

(defn truncate-to-zero
  [x]
  (if (> 0 x) 0 x))

(defn time-to-wait
  [start-time id]
  (truncate-to-zero (- id (mod start-time id))))

(defn map-ids-to-wait-time
  [info]
  (zipmap (:ids info)
          (map #(time-to-wait (:earliest-depart info) %) (:ids info))))

(defn solve-part-1
  ([] (solve-part-1 (parse-input-part-1 input)))
  ([info]
   (apply * (first (sort-by #(second %) (map-ids-to-wait-time info))))))


; 
; Look at earlier commits if you want to see a messy, long way of how I got here.
; But the short answer is: solving linear systems for integer answers and then nesting them
; 

(defn find-variable-of-first-integer-answer
  "Given an expression, finds the first positive integer answer,
   and returns the variable used to get that answer"
  [expression]
  (first (first (filter #(and (int? (second %)) (> (second %) 0))
                        (map #(vector % (expression %)) (range))))))
; This isn't great because I still have to go through millions for each one... 
; I'm approaching this slightly wrong. I have the right idea, but I think I want the lower number to be the numerator
; which gives it a much quicker chance of occuring first

(defn solve-equation
  "Given an equation in form c0x0 + b = c1x1 + d
   Returns a new expression that represents the sequence of positive integer answers to the equation above.
   For example, given [0 3] [-1 5] (which represents 3x0 = 5x1 - 1),
   this returns [9 15]"
  [[cons1 coeff1] [cons2 coeff2]]
  ; restructure the equation to find x1 (variable of second equation).
  ; then use that to produce the new constant (by plugging the expression into the original first expression). 
  ; i.e., x1 = (c0x0 + b - d) / c1 (solve for x1 that's an integer by iterating through x0 options). Then use the x0 option
  [(+ cons1 (* coeff1 (find-variable-of-first-integer-answer #(/ (- (+ (* coeff1 %) cons1) cons2) coeff2))))
   (* coeff1 coeff2)])

(defn solve-part-2
  ([] (solve-part-2 (parse-input-part-2 input)))
  ([indexed-ids]
   ; only need the constant of the final equation to get an answer
   (first
    (reduce solve-equation
            (map (fn [[x y]] [(- x) y]) indexed-ids))))) ; make all constants negative for proper equation