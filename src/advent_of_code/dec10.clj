(ns advent-of-code.dec10)
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

(defn solve-part-1
  ([] (solve-part-1 (parse-input input)))
  ([list]
   (let [diffs (find-differences (sort > (conj list 0)))] ; add 0 to account for outlet
     (*
      (count (filter #(= 1 %) diffs))
      (inc (count (filter #(= 3 %) diffs)))))))


; here's how I'll calculate the possiblities for part 2:
; It's really about multiplying when there are choices to be made (or finding the combinations)
; if a node only has one child, then there are no choices to be made (only 1). 
; if it has 2 then 2 combinations. 
; if it has 3, then 4 combinations
; but there is an interesting thing when you get 5 consecutive numbers like so (where the first and last number MUST be used):
; (0), 1, 2, 3, 4
; There are actually 6 additional combinations here:
; 0 1 4
; 0 1 2 4
; 0 1 3 4
; 0 2 4
; 0 2 3 4
; 0 3 4
; So we can make some assumptions and have a fairly simple algorithm:
; i) There are only differences of 1 or 3 between consecutive neighbors (hinted at in part 1 and confirmed using repl)
; ii) There are no groups of contiguous numbers that span more than 5 numbers (confirmed using repl)
; So go through list of numbers, count the groups of contiguous integers, and map that to the number of combinations:
; # in Group of Contig. Ints | Combinations
;                          1 | 1
;                          2 | 1
;                          3 | 2
;                          4 | 4
;                          5 | 7
; 
; Then multiply all of those together
; 
; I didn't actually come to this solution in this manner. I looked at the examples given, and noticed that 19208 breaks up into 2^3 * 7^4
; So I knew 7 had something to do with it, and came across it when working with the 1, 2, 3, 4, 5 example above


(defn group-to-combinations
  [group]
  (case group
    1 1
    2 1
    3 2
    4 4
    5 7))

(defn find-num-in-contiguous-groups
  ([list] (find-num-in-contiguous-groups (sort list) 1 '()))
  ([list current res]
   (if (empty? list)
     res
     (if (= (inc (first list)) (second list))
       (find-num-in-contiguous-groups (rest list) (inc current) res)
       (find-num-in-contiguous-groups (rest list) 1 (conj res current))))))

(defn solve-part-2
  ([] (solve-part-2 (conj (parse-input input) 0)))
  ([list] (apply * (map group-to-combinations (find-num-in-contiguous-groups (sort list))))))