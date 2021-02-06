(ns advent-of-code.core
  (:gen-class))
(require '[clojure.string :as str])
(require '[clojure.java.io :as io])

(def input (slurp (.getFile (io/resource "dec19_input.txt"))))

(defn parse-single-dependant
  [dependant]
  ; assume only literals are "a" or "b"
  (map #(if (or (= "\"a\"" %) (= "\"b\"" %)) (second %) (Integer/parseInt %))
       (str/split dependant #" ")))

(defn parse-dependents
  [dependents]
  (map parse-single-dependant (str/split dependents #" \| ")))

(defn parse-rule
  [rule]
  (let [a (str/split rule #": ")]
    [(Integer/parseInt (first a))
     (parse-dependents (second a))]))

(defn parse-rules
  [rules]
  (into {} (map parse-rule (str/split rules #"\n"))))

(defn parse-messages
  [messages]
  (str/split messages #"\n"))

(defn parse-input
  [in]
  (let [a (str/split in #"\n\n")]
    {:rules (parse-rules (first a))
     :messages (parse-messages (second a))}))

; end of parsing code
; 

; 1. for each list of dependents,
;     1a. if dependents are a char, return a string of char
;     1b. need to replace for each in list
;     this is done by going to 1 for each. 
;     
; I want it to look something like this.
; Given:
; 0: 1 2
; 1: "a"
; 2: 1 3 | 3 1
; 3: "b"
; 
; return '((\a) ((\a \b) (\b \a)))
; by going like 0 -> 1 -> '(a)
; then 2 -> 1 -> '(a) -> 3 -> '(ab)
; -> 3 -> '(b) -> 1 -> '(ba)   -> '('(ab) '(ba))
; 

(defn is-char?
  [c]
  (or (= '\a c) (= \b c)))

(defn expand-rules
  ([rules] (expand-rules rules 0))
  ([rules number]
   (let [all-dependents (get rules number)]
     (if (is-char? (first (first all-dependents)))
       (first (first all-dependents))
       (map (fn [dependents]
              (map #(expand-rules rules %) dependents))
            all-dependents)))))

; I basically need to make decisions about when to be OR
; 
; 1. For each possibility:
;     Go over each element
;     1a. If it's a character, append to all results
;     1b. If it's a list, go to 1 (to get a list of other possibilities)
;     and then append to all results
;     
; The basic case for this is '( (\a) (\b) ) right?
; Should return '("a" "b")
; And '(\a) should return ("a")
; So getting the comination of ("a") to ("a" "b") should give
; ("aa" "ab")


(declare combine)

(defn add-to-results
  [results new-results]
  (into #{} (for [r results x (map str new-results)] (str r x))))

; Jesus christ this method is messy, but it works.
; 
; The division of labor between combine/combine helper is supposed to help with different options.
; combine looks at all options, while combine-helper is meant to look at a single option and combine it,
; using the combine function if needed to find other options
(defn combine-helper
  "Takes in a list (possibly nested) that represents a possibility."
  [possibility results]
  (if (is-char? possibility)
    (add-to-results results [possibility])
    (if (empty? possibility)
      results
      (combine-helper (rest possibility)
                      (add-to-results results
                                      (if (is-char? (first possibility))
                                        [(first possibility)]
                                        (combine (first possibility))))))))

(defn combine
  ([possibilities] (reduce into #{} (combine possibilities '(""))))
  ([possibilities results]
   (map #(combine-helper % results) possibilities)))

(defn count-matching
  [all-possibilities messages]
  (count (filter #(contains? all-possibilities %) messages)))

(defn solve
  ([]
   (let [in (parse-input input)]
     (solve (:rules in) (:messages in))))
  ([rules messages]
   (count-matching (combine (expand-rules rules)) messages)))


; For part 2, it's important to see that the two rules that have changed (8 and 11) are the rule for 0:
; (i.e., 0: 8 11)
; So, this means, all messages that matched before will still match.
; And only new ones are added.

; So this means, the new ones that are added will: have 42 once or more at the beginning.
; And will have 31 once or more at the end. (and 42 one or more times before that).
; So overall, it's (42){2,} (31)+
; (i.e., 42 twice or more times, and then 31 once or more times)

; Another interesting thing is that all matches of both 42 and 31 have the exact same length.
; That is, `(map count (combine (expand-rules (:rules (parse-input input)) 42)))` shows all 8
; (and same for 31)

(def rule-message-length 8)

; So maybe for this time, for each message,
;        1. See how many times it matches 42 (make sure it's at least twice)
;        2. See how many times it matches 31 (must be at least once)
;        3. Then return if it's a valid message.
;  Important notes: I'll have to reduce the message by 8 letters each time I find it matches one.
;  
;  OHHHHH!!! But for each set of 31, there must be the same amount (plus 1) for 42 

(defn beginning-matches?
  [possibilities message]
  (contains? possibilities (subs message 0 rule-message-length)))

(defn count-42-matches-at-beginning
  ([message possibilities] (count-42-matches-at-beginning message possibilities 0))
  ([message possibilities count]
   (if (or (empty? message) (not (beginning-matches? possibilities message)))
     count
     (count-42-matches-at-beginning (subs message rule-message-length) possibilities (inc count))))
  )

; TODO: has some duplication with the one above but w/e
(defn count-31-matches-at-beginning
  "Returns nil if the complete message doesn't match the rule 31 (any number of times)"
  ([message possibilities] (count-31-matches-at-beginning message possibilities 0))
  ([message possibilities count]
   (cond
     (empty? message) count
     (not (beginning-matches? possibilities message)) nil
     :else (count-31-matches-at-beginning (subs message rule-message-length) possibilities (inc count))
     )))

(defn message-matches-part-2?
  [message possibilities]
  (let [matches-42 (count-42-matches-at-beginning message (get possibilities 42))]
    (if-let [matches-31
             (count-31-matches-at-beginning (subs message (* rule-message-length matches-42)) (get possibilities 31))]
      (and (>= matches-31 1) (>= matches-42 (inc matches-31)) (or (println message " " matches-42 " " matches-31) true))
      false)))

(defn solve-part-2
  ([] (let [in (parse-input input)]
        (solve-part-2 (:rules in) (:messages in))))
  ([rules messages]
   (let [possibilities {42 (combine (expand-rules rules 42))
                        31 (combine (expand-rules rules 31))}]
     
     (filter #(message-matches-part-2? % possibilities) messages)
     )))

(defn -main
  "I don't do a whole lot ... yet."
  [& args]
  (println "Hello, World!"))
