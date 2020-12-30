(ns advent-of-code.core
  (:gen-class))
(require '[clojure.string :as str])
(require '[clojure.java.io :as io])

(def input (slurp (.getFile (io/resource "dec13_input.txt"))))

(defn parse-ids-part-1
  [ids]
  (map #(Integer/parseInt %) (str/split ids #",")))

(defn parse-input-part-1
  [in]
  (let [a (str/split in #"\n")]
    {:earliest-depart (Integer/parseInt (first a)),
     :ids (parse-ids-part-1 (second a))}))

(defn parse-id-part-2
  [index id]
  [index (if (= "x" id) :x (Integer/parseInt id))])

(defn parse-input-part-2
  [in]
  (filter #(not= :x (second %)) (map-indexed parse-id-part-2 (str/split (second (str/split in #"\n")) #","))))

(defn truncate-to-zero
  [x]
  (if (> 0 x) 0 x))

(defn time-to-wait
  [start-time id]
  (truncate-to-zero (- id (mod start-time id))))

(defn map-ids-to-wait-time
  [info]
  (zipmap (:ids info)
   (map #(time-to-wait (:earliest-depart info) %) (:ids info))))

(defn solve-part-1
  ([] (solve-part-1 (parse-input-part-1 input)))
  ([info]
   (apply * (first (sort-by #(second %) (map-ids-to-wait-time info))))))

(defn start-time-valid?
  "If the bus departs (first indexed-id) minutes after this time"
  [time indexed-id]
  (println time)
  (let [index (first indexed-id)
        id (second indexed-id)]
    (or (= 0 (mod (- index (time-to-wait time id)) id)))))

; I need to do set reduction.
; I need the set of all multiples of (first number)
; Then I need the intersection of that set with the (mod % (second number)) == 1, etc.
; There is a problem when index >= number, though... 
; so it needs to be (in infix expressions):
; (index - time-to-wait) % id == 0

(defn solve-part-2
  ([] (solve-part-2 (parse-input-part-2 input)))
  ([indexed-ids]
   (solve-part-2 (rest indexed-ids) (iterate #(+' % (second (first indexed-ids))) (second (first indexed-ids)))))
  ([indexed-ids possible-times]
   (if (empty? indexed-ids)
    (first possible-times)
    (solve-part-2 (rest indexed-ids) 
                  (filter #(start-time-valid? % (first indexed-ids)) possible-times)))))

(defn -main
  "I don't do a whole lot ... yet."
  [& args]
  (println "Hello, World!"))
