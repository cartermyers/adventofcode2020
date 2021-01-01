(ns advent-of-code.core
  (:gen-class))
(require '[clojure.string :as str])
(require '[clojure.java.io :as io])

(def input (slurp (.getFile (io/resource "dec13_input.txt"))))

(defn parse-ids-part-1
  [ids]
  (map #(Integer/parseInt %) (str/split ids #",")))

(defn parse-input-part-1
  [in]
  (let [a (str/split in #"\n")]
    {:earliest-depart (Integer/parseInt (first a)),
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


; find an t such that
; t % x0 == 0, (t + 1) % x1 == 0, (t + 2) % x2 == 0,
; ... (t + n) % xn == 0
; 
; maybe I need to do a dynamic programming thing...
; like pick z0, then see if it works for the next one.
; if it does, pick z1 and see if it works for the next one.
; if it doesn't, go back to the first one and pick again.
; I think this is okay, because I only have 9 numbers to really select...
; Solving a system with 3 bus ids, is really like solving two systems (each with two ids) and finding an overlap.
; For example,
; [0 3] [1 5] [2 7]
; Is like solving [0 5] [1 7] and then solving [0 3] [1 5] and finding the first overlap
; 
; For [0 5] [1 7]
; 5z1 = 7z2 - 1 => z1 = (7z2 - 1) / 5 => z2 = 3, 8, 13, 18, 23, 28, 33, 38, 43, 48, 53, 58, 63, 68 (etc.) and z1 = 4, 11, 18, 25, 32, 39, 46, 53 ...
; And notice how these sequences start with an offset (for z2 it's 3 and z1 it's 4) and continue by their counterpart's factor (5 and 7 respectively).
; 
; For [0 3] [1 5]
; 3z0 = 5z1 - 1 => z0 = (5z1 - 1) / 3 => z1 = 2, 5, 8, 11, 14, 17, 20, 23, 26, 29, 32, 35, 38, 41, 44, 47, 50, 53 ... and z0 = 3, 8, 13, 18, 23 ...
; 
; 
; So here, we find that z2 = 8 corresponds to z1 = 11, which corresponds to z0 = 18 (based on sequences)
; 
; For algorithm, look at first sequence of z1 (7n + 4). Is 4 in the next sequence of z1 (3n + 2)? No. Is 11? Yes!
; So pick the corresponding index in the z0 sequence (5n + 3).
; 
; Resulting possibilities: 11, 32, 53
; 
; 21x + 11. How can we derive this? + 11 because it's the first one in both sequences. 21 because it's when 7 and 3 are multiplied
; Could be 3 * 7 * x + 3 + 8 ? 8 is the second term from the last sequence (5x + 3)
; 
; Let's add a fourth id: [5 13]
; Let's try that:
; 7z2 - 2 = 13z5 - 5 => z2 = (13z5 - 3) / 7 => z5 = 7n + 4 and z2 = 13n + 7
; Or z5 = 4, 11, 18, 25 (curriously, same as z1) ... and z2 = 7, 20, 33, 46, 59
; 
; Going back to original, we found these sequences
; z2 = 5n + 3
; z1 = 7n + 4 (from z2) and 3n + 2 (from z0)
; z0 = 5n + 3
; 
; Does this mean that 3 at then end -> 3(3 - 1) + 2 = 8 (i.e., three - 1 term in the last sequence)?
; 
; 
; 21n + 11 = 11, 32, 53, 64, 85, 106, 127, 148, 169, 190, 211
; z2 = 7, 20, 33, 46, 59, 72
; 
; 21n + 11 = 13n + 7
; 
; 
; Maybe this should be the base unit? 3 instead of 2? ...
; Or better yet, we create a sequence of possible ones. This is what I was conceptuallizing first, but I think this can be a more efficient way.
; So with input -> next input
; '([0 3] [1 5] [2 7]) #(x) -> '([1 5] [2 7]) #(3 * x) -> '([2 7]) #(5x + 3) -> '([2 7]) #(15x + 9) -> '() #(105z + 54)
; 
; 3z0 = 5z1 - 1 => z0 = 5x + 3 (since offset is 3)
; For next one: 15z0 + 9 = 7z2 - 2 => z0 = (7z2 - 11) / 15 so z2 = 8, z0 = 3, 10, 17
; => 15 * (7n + 3) + 9 = 105n + 54

; Try once more with 13:
; '([5 13]) #(105x + 54) -> '() #()
; 
; 105z0 + 54 = 13z5 - 5 => z0 = (13z5 - 59) / 105 => z5 = 53, z0 = 6 (so 13n + 6)
; => 105 * (13n + 6) + 54 = 1365n + 630 + 54 = 1365n + 684
; 
; Or in expanded version:
; 3 * (3 + 5 * (3 + 7 * (13n + 6)))
; when n = 0:
; 3 * (3 + 5 * (3 + 7 * (6))) = 684
; 
; So finally, the answer is to use a nested equation, solving for a integer answer at each step
; 

(defn find-first-integer-answer
  "Given an expression, finds the first positive integer answer"
  [expression]
  (first (filter #(and (int? %) (> % 0)) 
                 (map expression (range)))))

(defn solve-equation
  "Given an equation in form c0x0 + b = c1x1 + d
   Returns a new expression that represents the sequence of positive integer answers to the equation above.
   For example, given [0 3] [-1 5] (which represents 3x0 = 5x1 - 1),
   this returns [9 15]"
  [[cons1 coeff1] [cons2 coeff2]]
  ; restructure the equation to find x0 (variable of first equation)
  ; then use that to produce the new constant (by plugging the expression into the original first expression)
  [(+ cons1 (* coeff1 (find-first-integer-answer #(/ (- (+ (* coeff2 %) cons2) cons1) coeff1))))
   (* coeff1 coeff2)]
  )

(defn solve-part-2
  ([] (solve-part-2 (parse-input-part-2 input)))
  ([indexed-ids]
   ; only need the constant of the final equation to get an answer
   (first 
    (reduce solve-equation 
            (map (fn [[x y]] [(- x) y]) indexed-ids))))) ; make all constants negative for proper equation

(defn -main
  "I don't do a whole lot ... yet."
  [& args]
  (println "Hello, World!"))
