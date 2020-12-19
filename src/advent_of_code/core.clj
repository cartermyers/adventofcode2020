(ns advent-of-code.core
  (:gen-class))
(require '[clojure.java.io :as io])
(require '[clojure.string :as str])

(def input (slurp (.getFile (io/resource "dec6_input.txt"))))


(defn parse-group
  "Returns a set of characters that contain the characters of each answered question"
  [group]
  (into #{} (filter #(not= \newline %) group)))

(defn parse-input
  [in]
  (map parse-group (str/split in #"\n\n")))

; now find sum of all yeses in groups

(defn find-count
  ([]
   (find-count (parse-input input)))
  ([groups]
   (reduce + (map count groups))))

(defn -main
  "I don't do a whole lot ... yet."
  [& args]
  (println "Hello, World!"))
