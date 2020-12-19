(ns advent-of-code.dec6)

(require '[clojure.java.io :as io])
(require '[clojure.string :as str])
(require '[clojure.set])

(def input (slurp (.getFile (io/resource "dec6_input.txt"))))

(defn parse-person
  [person]
  (into #{} person))

(defn parse-group
  "Returns a set of characters to which every person in the group answered yes"
  [group]
  (reduce clojure.set/intersection (map parse-person (str/split group #"\n"))))

(defn parse-input
  [in]
  (map parse-group (str/split in #"\n\n")))

; now find sum of all yeses in groups

(defn find-count
  ([]
   (find-count (parse-input input)))
  ([groups]
   (reduce + (map count groups))))