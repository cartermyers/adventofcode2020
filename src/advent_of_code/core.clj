(ns advent-of-code.core
  (:gen-class))
(require '[clojure.java.io :as io])
(require '[clojure.string :as str])

(def input (slurp (.getFile (io/resource "dec22_input.txt"))))

(defn parse-deck
  [deck]
  ;discard the first line (not important)
  (map #(Integer/parseInt %) (rest (str/split deck #"\n"))))

(defn parse-input
  [in]
  (let [decks (str/split in #"\n\n")]
    {:player-1 (parse-deck (first decks))
     :player-2 (parse-deck (second decks))}
    ))


(defn compare-draws
  [draws]
  (if (> (first draws) (second draws))
    :player-1
    :player-2))

(defn remove-tops
  [decks]
  (into {} (map (fn [[k v]] [k (rest v)]) decks)))

(defn play-round
  [decks]
  (let [draws (map first (vals decks))]
    (update-in (remove-tops decks) [(compare-draws draws)] #(concat % (reverse (sort draws)))))
  )

(defn calc-score
  [winning-deck]
  (reduce + (map-indexed #(* %2 (inc %1)) (reverse winning-deck))))

(defn remove-empty-decks
  [decks]
  (dissoc decks (first
           (first (filter (fn [[_ v]] (empty? v)) decks)))))

(defn play-game
  [d]
  (let [decks (remove-empty-decks d)]
    (if (= 1 (count decks))
      (calc-score (second (first decks)))
      (play-game (play-round decks)))))

(defn -main
  "I don't do a whole lot ... yet."
  [& args]
  (println "Hello, World!"))
