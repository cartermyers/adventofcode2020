(ns advent-of-code.core
  (:gen-class))
(require '[clojure.string :as str])
(require '[clojure.java.io :as io])

(def input (slurp (.getFile (io/resource "dec14_input.txt"))))

(defn parse-mask
  [mask]
  {:mask 
   (keep-indexed #(when (not (= \X %2)) (vector (- 36 %1 1) (Integer/parseInt (str %2))))
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

(defn mask-bit
  [[k bit] value]
  (let [is-set (kth-bit-set? k value)]
    (case bit
      0 (if is-set (- value (power-of-2 k)) value)
      1 (if is-set value (+ value (power-of-2 k))))))

(defn apply-bit-mask
  [mask value]
  (if (empty? mask)
    value
    (apply-bit-mask (rest mask) (mask-bit (first mask) value))))

(defn write-memory
  [memory address value]
  (assoc memory address (apply-bit-mask (:mask memory) value)))

(defn execute-instruction
  [instruction memory]
  (if (contains? instruction :mask)
    (assoc memory :mask (:mask instruction))
    (write-memory memory (:address instruction) (:value instruction))))

(defn execute
  ([instructions] (execute instructions {:mask (repeat 36 "0")}))
  ([instructions memory]
   (if (empty? instructions)
     memory
     (execute (rest instructions) (execute-instruction (first instructions) memory)))))

(defn solve
  ([] (solve (parse-input input)))
  ([instructions] (reduce +' (vals (dissoc (execute instructions) :mask)))))

(defn -main
  "I don't do a whole lot ... yet."
  [& args]
  (println "Hello, World!"))
