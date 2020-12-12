(ns advent-of-code.core
  (:gen-class))
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
  [in]
  (count (filter valid-password? (parse-input in))))

(defn -main
  "I don't do a whole lot ... yet."
  [& args]
  (println "Hello, World!"))
