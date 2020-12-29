(ns advent-of-code.core
  (:gen-class))
(require '[clojure.java.io :as io])
(require '[clojure.string :as str])

(def input (slurp (.getFile (io/resource "dec12_input.txt"))))

(defn parse-direction
  [d]
  (case d
    \N :north
    \S :south
    \E :east
    \W :west
    \L :left
    \R :right
    \F :forward))

(defn parse-line
  [line]
  {:direction (parse-direction (get line 0)), 
   :magnitude (Integer/parseInt (subs line 1))})

(defn parse-input
  [in]
  (map parse-line (str/split in #"\n")))

; assume turns only in 90 degree intervals (e.g., 90, 180, 270, etc)
(defn turn 
  [current direction]
    (case direction
      :left (case current
              :north :west
              :west :south
              :south :east
              :east :north)
      :right (case current
              :north :east
              :east :south
              :south :west
              :west :north)))


(defn update-facing
  [position instruction]
  (if (= 0 (:magnitude instruction))
    position
    (update-facing (update position :facing #(turn % (:direction instruction))) 
                   (update instruction :magnitude #(- % 90)))))

(defn move
  [position instruction]
  (case (:direction instruction)
    :north (update position :y #(+ % (:magnitude instruction)))
    :south (update position :y #(- % (:magnitude instruction)))
    :east (update position :x #(+ % (:magnitude instruction)))
    :west (update position :x #(- % (:magnitude instruction)))
    :forward (move position (update instruction :direction (fn [x] (:facing position))))
    :left (update-facing position instruction)
    :right (update-facing position instruction)))

(defn manhattan-dist
  "From the origin at (0, 0)"
  [position]
  (+ (Math/abs (:x position)) 
     (Math/abs (:y position))))

(defn solve
  ([] (solve (parse-input input)))
  ([instructions] (solve instructions {:x 0, :y 0, :facing :east}))
  ([instructions position]
   (if (empty? instructions)
     (manhattan-dist position)
     (solve (rest instructions) (move position (first instructions))))))

(defn -main
  "I don't do a whole lot ... yet."
  [& args]
  (println "Hello, World!"))
