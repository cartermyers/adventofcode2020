(ns advent-of-code.core
  (:gen-class))
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

(defn add-or-mult
  "Given an expression with no parentheses, 
   and only 2 terms"
  [expr]
  ((case (second expr)
     :add +
     :mult *)
   (first expr) (nth expr 2)))

(defn evaluate
  "Given an expression with no parentheses,
   and an arbitrary number of terms."
  ([expr]
   (let [vec (into [] expr)]
     (if (> 3 (count vec))
       (first vec)
       (evaluate (into [(add-or-mult vec)] (drop 3 vec)))))))

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
       (evaluate-with-parentheses (rest expr) (conj stack (first expr)))
       )))
  )

(defn solve
  ([] (solve (parse-input input)))
  ([exprs] 
   (reduce + (map evaluate-with-parentheses exprs))))


(defn -main
  "I don't do a whole lot ... yet."
  [& args]
  (println "Hello, World!"))
