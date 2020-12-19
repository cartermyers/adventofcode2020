(ns advent-of-code.core
  (:gen-class))
(require '[clojure.java.io :as io])
(require '[clojure.string :as str])

(def input (slurp (.getFile (io/resource "dec5_input.txt"))))


(defn parse-rows
  [rows]
  (map #(= \B %) rows))

(defn parse-columns
  [columns]
  (map #(= \R %) columns))

(defn parse-seat
  "Returns a map containing the :rows and :columns instructions (in ints)"
  [seat]
  {:rows (parse-rows (subs seat 0 7)) 
   :columns (parse-columns (subs seat 7))})

(defn parse-input
  [in]
  (map parse-seat (str/split in #"\n")))


; end of parsing
; begin binary conversion
; 

(defn power-of-2
  "Only works for non-negative numbers"
  ([pow] (power-of-2 pow 1))
  ([pow total]
   (if (= 0 pow)
     total
     (power-of-2 (dec pow) (* total 2)))))

(defn decode-bit
  "Given a bit and it's position in the binary number,
   return its value if it's 1 else 0"
  [bit pos]
  (if bit
    (power-of-2 (dec pos))
    0))

(defn bits-to-int
  "Given a list that represents a binary number (with bools),
   returns the integer value"
  ([bits]
   (bits-to-int bits 0))
  ([bits total]
   (if (empty? bits)
     total
     (bits-to-int (rest bits) 
                  (+ total (decode-bit (first bits) (count bits)))))
   ))

; now for the specific problem:

(defn get-seat-id
  [seat]
  (+ (* 8 (bits-to-int (seat :rows))) 
     (bits-to-int (seat :columns))))

(defn get-highest-seat-id
  ([]
   (get-highest-seat-id (parse-input input)))
  ([seats]
   (apply max (map get-seat-id seats))))

(defn is-one-seat-apart?
  [seat-id1 seat-id2]
  (= seat-id1 (dec seat-id2)))

(defn find-my-seat
  "Sort all of the seats by ID, then find the gap"
  ([]
   (find-my-seat (sort (map get-seat-id (parse-input input)))))
  ([seat-ids]
   (if (is-one-seat-apart? (first seat-ids) (second seat-ids))
     (find-my-seat (rest seat-ids))
     (inc (first seat-ids)))))

(defn -main
  "I don't do a whole lot ... yet."
  [& args]
  (println "Hello, World!"))
