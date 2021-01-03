(ns advent-of-code.core
  (:gen-class))
(require '[clojure.string :as str])
(require '[clojure.java.io :as io])
(require '[clojure.set])

(def input (slurp (.getFile (io/resource "dec16_input.txt"))))

(defn parse-range
  [rangeIn]
  (let [r (map #(Integer/parseInt %) (str/split rangeIn #"-"))]
    (range (first r) (inc (second r)))))

(defn parse-rule
  [rule]
  (let [r (str/split rule #"(: )|( or )")]
    {(keyword (str/replace (first r) #" " "-"))
     (reduce into #{} (map parse-range (rest r)))})) ;not very efficient, but might make implementation easy

(defn parse-rules
  [rules]
  (reduce into {} (map parse-rule (str/split rules #"\n"))))

(defn parse-ticket
  [ticket]
  (map #(Integer/parseInt %) (str/split ticket #",")))

(defn parse-tickets
  [tickets]
  (map parse-ticket (rest (str/split tickets #"\n"))))

(defn parse-input
  [in]
  (let [sections (str/split in #"\n\n")]
    {:rules (parse-rules (first sections))
     :my-ticket (first (parse-tickets (second sections)))
     :nearby-tickets (parse-tickets (nth sections 2))}))


(defn valid-ticket?
  "Given a ticket, returns true if field couldn't possibly be valid"
  [ticket all-valid-values]
  (every? #(contains? all-valid-values %) ticket))

(defn valid-tickets
  [tickets rules]
  (let [all-rules-values (apply clojure.set/union (vals rules))]
    (filter #(valid-ticket? % all-rules-values) tickets)))

(defn valid-rules-for-column
  "Given a specific position, return the rules that may apply to that position.
   That is, all values in that position in all tickets are within range for that rule."
  [tickets rules position]
  (let [vals-at-pos (set (map #(nth % position) tickets))]
    (map first (filter #(clojure.set/subset? vals-at-pos (second %)) rules))))

(defn remove-rules-from-possibilities
  [possibilites rules]
  (map (fn [coll] (remove #(some #{%} rules) coll)) possibilites))

(defn assign-rules-to-columns
  "Looking at the code, it looks like the options for all position follow (set (range 1 21)) (not in that order though).
   So I'll start by taking any positions that only have 1 rule, 
   assigning it the position, 
   and then removing those rules from all others.
   Repeat until there are no more (hopefully)"
  ([applicable-rules-at-position]
   (assign-rules-to-columns applicable-rules-at-position {}))
  ([rules-at-positions res]
   (if (every? empty? rules-at-positions)
     res
     (let [identified (keep-indexed #(if (= 1 (count %2)) [(first %2) %1]) rules-at-positions)]
       (assign-rules-to-columns
        (remove-rules-from-possibilities rules-at-positions (map first identified))
        (into res identified))))))

(defn count-depature-fields
  [my-ticket rules-to-columns]
  (apply * (map #(nth my-ticket (second %)) 
                (filter #(str/includes? (name (first %)) "departure") rules-to-columns))))

(defn solve
  ([] (solve (parse-input input)))
  ([in]
   (count-depature-fields (:my-ticket in)
                          (assign-rules-to-columns
                           (let [tickets (valid-tickets (:nearby-tickets in) (:rules in))]
                             (map #(valid-rules-for-column tickets (:rules in) %) (range (count (first tickets)))))))))

(defn -main
  "I don't do a whole lot ... yet."
  [& args]
  (println "Hello, World!"))
