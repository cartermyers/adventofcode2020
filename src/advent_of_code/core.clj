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

(defn deg-to-rad
  [deg]
  (* deg (/ Math/PI 180)))

; use rotation matrix:
(defn rotate
  "Rotate a point (at coords) around the origin by radians"
  [coords radians]
  {:x (- (* (:x coords) (Math/cos radians)) (* (:y coords) (Math/sin radians))), 
   :y (+ (* (:x coords) (Math/sin radians)) (* (:y coords) (Math/cos radians)))})

(defn rotate-waypoint
  [position angle]
  (update position :waypoint #(rotate % (deg-to-rad angle))))

(defn update-waypoint
  [position axis direction magnitude]
  (update position :waypoint #(update % axis (fn [a] (direction a magnitude)))))

(defn update-axis
  [position axis magnitude]
  (update position axis #(+ % (* (axis (:waypoint position)) magnitude))))

(defn update-position
  [position magnitude]
  (update-axis (update-axis position :x magnitude)
               :y magnitude))

(defn move
  [position instruction]
  (case (:direction instruction)
    :north (update-waypoint position :y + (:magnitude instruction))
    :south (update-waypoint position :y - (:magnitude instruction))
    :east (update-waypoint position :x + (:magnitude instruction))
    :west (update-waypoint position :x - (:magnitude instruction))
    :forward (update-position position (:magnitude instruction))
    :left (rotate-waypoint position (:magnitude instruction))
    :right (rotate-waypoint position (- (:magnitude instruction)))))

(defn manhattan-dist
  "From the origin at (0, 0)"
  [position]
  (+ (Math/abs (:x position)) 
     (Math/abs (:y position))))

(defn solve
  ([] (solve (parse-input input)))
  ([instructions] (solve instructions {:x 0, :y 0, :waypoint {:x 10, :y 1}}))
  ([instructions position]
   (if (empty? instructions)
     (manhattan-dist position)
     (solve (rest instructions) (move position (first instructions))))))

(defn -main
  "I don't do a whole lot ... yet."
  [& args]
  (println "Hello, World!"))
