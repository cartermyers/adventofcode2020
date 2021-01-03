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


(defn invalid-ticket-values
  "Given a ticket, returns the sum of values of fields that couldn't possibly be valid"
  [ticket all-valid-values]
  (apply + (filter #(not (contains? all-valid-values %)) ticket)))


(defn solve
  ([] (solve (parse-input input)))
  ([in]
   (apply + (map #(invalid-ticket-values % (apply clojure.set/union (vals (:rules in)))) 
                 (:nearby-tickets in)))))

(defn -main
  "I don't do a whole lot ... yet."
  [& args]
  (println "Hello, World!"))
