(ns advent-of-code.dec2)
(require '[clojure.java.io :as io])
(require '[clojure.string :as str])


(def input (slurp (.getFile (io/resource "dec2_input.txt"))))

;;; first, parse the input
;;; 

(defn parse-range
  "Returns a list of int, denoting the range"
  [range]
  (map #(Integer. %) (str/split range #"-")))

(defn parse-line
  "Given a line as in:
   1-3 a: abcde
   returns a map with {:range (1 3), :letter \\a, :password 'abcde'}"
  [line]
  (let [tokens (str/split line #" ")]
    {:range (parse-range (first tokens))
     :letter (first (second tokens))
     :password (last tokens)}))

(defn parse-input
  "Given a string, delimited by endlines,
   returns a list of maps, each with {:range :letter :password}"
  [s]
  (map parse-line (str/split s #"\n")))

;;;; logic to check if password is valid

(defn in-range?
  "Returns true if n is a number and between the numbers in range"
  [range n]
  (and (number? n) (<= (first range) n) (<= n (second range))))


(defn valid-password?
  "Given a password object, check if it meets the requirements
  (i.e., contains the specified letter in the appropriate range)"
  [password]
  (in-range? (:range password) (get (frequencies (:password password)) (:letter password))))

;; see how many valid passwords are in input:

(defn count-valid-passwords
  ([in]
   (count-valid-passwords valid-password?))
  ([in f]
   (count (filter f (parse-input in)))))


; part-2 logic
; 

(defn xor
  "Exactly one value is true."
  [a b]
  (and (or a b) (not (and a b))))

(defn char-at?
  "Returns true if c is in the string at index i using 1-based index"
  [s c i]
  (= (get s (dec i)) c))

(defn valid-password-2?
  [password]
  (apply xor (map #(char-at? (:password password) (:letter password) %) (:range password))))
