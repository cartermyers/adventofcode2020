(ns advent-of-code.dec14)
(require '[clojure.string :as str])
(require '[clojure.java.io :as io])

(def input (slurp (.getFile (io/resource "dec14_input.txt"))))

(defn parse-mask
  [mask]
  {:mask
   (keep-indexed #(when (not (= \0 %2)) (vector (- 36 %1 1) (if (= \X %2) :X 1)))
                 (second (str/split mask #" = ")))})

(defn parse-memory
  [mem]
  {:address (Integer/parseInt (second (str/split mem #"\[|\]")))
   :value (Integer/parseInt (second (str/split mem #" = ")))})

(defn parse-instruction
  [in]
  (if (str/starts-with? in "mask")
    (parse-mask in)
    (parse-memory in)))

(defn parse-input
  [in]
  (map parse-instruction (str/split in #"\n")))

; "business" logic:

(def power-of-2
  ; minor: still not completely optimized. 
  ; Could memoize a recursive function or bit shift or something for MAXIMUM EFFICIENCY
  (memoize
   (fn [pow]
     (reduce (fn [x y] (*' 2 x)) 1 (range pow)))))

(defn kth-bit-set?
  "Algorithm taken from somewhere online. 0-indexed"
  [k value]
  (= 1 (bit-and 1 (bit-shift-right value k))))

(defn force-bit-to-zero
  [k value]
  (if (kth-bit-set? k value) (- value (power-of-2 k)) value))

(defn force-bit-to-one
  [k value]
  (if (kth-bit-set? k value) value (+ value (power-of-2 k))))

(defn create-floating-possibilities
  [k values]
  (reduce #(into %1 (vector (force-bit-to-zero k %2) (force-bit-to-one k %2))) [] values))

(defn mask-bit
  [[k bit] values]
  (case bit
    :X (create-floating-possibilities k values)
    1 (mapv #(force-bit-to-one k %) values)))

(defn apply-bit-mask
  [mask values]
  (if (empty? mask)
    values
    (apply-bit-mask (rest mask) (mask-bit (first mask) values))))

(defn write-memory
  [memory address value]
  (reduce #(assoc %1 %2 value) memory (apply-bit-mask (:mask memory) (vector address))))

(defn execute-instruction
  [instruction memory]
  (if (contains? instruction :mask)
    (assoc memory :mask (:mask instruction))
    (write-memory memory (:address instruction) (:value instruction))))

(defn execute
  ([instructions] (execute instructions {:mask '()}))
  ([instructions memory]
   (if (empty? instructions)
     memory
     (execute (rest instructions) (execute-instruction (first instructions) memory)))))

(defn solve
  ([] (solve (parse-input input)))
  ([instructions] (reduce +' (vals (dissoc (execute instructions) :mask)))))