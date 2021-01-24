(ns advent-of-code.dec18)
(require '[clojure.string :as str])
(require '[clojure.java.io :as io])

(def input (slurp (.getFile (io/resource "dec18_input.txt"))))

(defn parse-token
  [token]
  (case token
    \+ :add
    \* :mult
    \( :open
    \) :close
    (Integer/parseInt (str token))))

; assumes that all input numbers will be single digits
(defn parse-expression
  [expr]
  (map parse-token (str/join (str/split expr #"\s+"))))

(defn parse-input
  [in]
  (map parse-expression (str/split in #"\n")))

; end of parsing

; NOTE!!!!!!!!!!
; This code is poorly written. There are definitely better ways to do this code.
; But I wanted to do this without peeking at anything so enter at your own risk.

; E -> <number> [(+|*) E] | (E)

(defn evaluate-addition
  "A hack to evaluate addition first.
   Either evaluates the addition or returns the same three terms"
  [op expr]
  (if (= :add (first expr))
    [(+ op (second expr))]
    (into [op] (take 2 expr))))

(defn evaluate-all-addition
  "Given an expression with no parentheses,
   and an arbitrary number of terms."
  ([expr] (evaluate-all-addition [(first expr)] (rest expr)))
  ([res expr]
   (if (> 2 (count expr))
     (into res expr)
     (evaluate-all-addition
      (into (into [] (drop-last res)) (evaluate-addition (last res) expr))
      (drop 2 expr)))))

(defn evaluate-all-multiplication
  "Make the terrible assumption that it's all multiplication"
  [expr]
  (if (> 3 (count expr))
    (first expr)
    (evaluate-all-multiplication
     (into [(* (first expr) (nth expr 2))] (drop 3 expr)))))

(defn evaluate
  [expr]
  (evaluate-all-multiplication (evaluate-all-addition expr)))

(defn evaluate-parenthetical-expr
  "Just a hlper function to make evaluate-with-parentheses a bit clearer.
   Evaluates the stack until the next open parentheses
   and puts the new value on top (while removing all of the old ones)"
  [stack]
  (conj (rest (drop-while #(not= :open %) stack)) ; use rest to remove the :open 
        (evaluate (reverse (take-while #(not= :open %) stack)))))

(defn evaluate-with-parentheses
  ([expr] (evaluate-with-parentheses expr '()))
  ([expr stack]
   (if (empty? expr)
     (evaluate (reverse stack))
     (if (= :close (first expr))
       ; if close, need to evaluate until we see the next (
       (evaluate-with-parentheses (rest expr) (evaluate-parenthetical-expr stack))
       (evaluate-with-parentheses (rest expr) (conj stack (first expr)))))))

(defn solve
  ([] (solve (parse-input input)))
  ([exprs]
   (reduce + (map evaluate-with-parentheses exprs))))

