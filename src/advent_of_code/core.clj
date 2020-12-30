(ns advent-of-code.core
  (:gen-class))
(require '[clojure.string :as str])
(require '[clojure.java.io :as io])

(def input (slurp (.getFile (io/resource "dec13_input.txt"))))

(defn parse-ids
  [ids]
  (map #(Integer/parseInt %) (filter #(not= "x" %) (str/split ids #","))))

(defn parse-input
  [in]
  (let [a (str/split in #"\n")]
    {:earliest-depart (Integer/parseInt (first a)),
     :ids (parse-ids (second a))}))

(defn truncate-to-zero
  [x]
  (if (> 0 x) 0 x))

(defn time-to-wait
  [earliest-depart id]
  (truncate-to-zero (- id (mod earliest-depart id))))

(defn map-ids-to-wait-time
  [info]
  (zipmap (:ids info)
   (map #(time-to-wait (:earliest-depart info) %) (:ids info))))

(defn solve
  ([] (solve (parse-input input)))
  ([info]
   (apply * (first (sort-by #(second %) (map-ids-to-wait-time info))))))

(defn -main
  "I don't do a whole lot ... yet."
  [& args]
  (println "Hello, World!"))
