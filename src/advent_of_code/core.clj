(ns advent-of-code.core
  (:gen-class))
(require '[clojure.string :as str])

(def input "#####..#
#..###.#
###.....
.#.#.#..
##.#..#.
######..
.##..###
###.####")

(defn parse-row
  [row] (mapv #(= \# %) row))

(defn parse-input
  [in]
  (mapv parse-row (str/split in #"\n")))

; Jesus christ, this code is a mess...
; I'm thinking too much about indices and not in a functional way. 
; But I've always had problem thinking about conway's game of life like that.
; I think I should just change it all to a map with all coordinates.
; And keep track of min/max coordinates

(def inc-x #(update % :x inc))
(def dec-x #(update % :x dec))
(def inc-y #(update % :y inc))
(def dec-y #(update % :y dec))
(def inc-z #(update % :z inc))
(def dec-z #(update % :z dec))

(defn reduce-to-set
  [original funcs]
  (reduce #(apply conj %1 (map %2 original)) original funcs))

(defn get-all-neighbor-coordinates
  ; need to go over coordinates with dec-z, inc-z, and identity
  ; then go over those results with y,
  ; then go over those results with x
  ; then remove the full identity
  [coordinate]
  (disj
   (reduce reduce-to-set #{coordinate} [[inc-z dec-z identity]
                                         [inc-y dec-y identity]
                                         [inc-x dec-x identity]])
   coordinate))

(defn active?
  [cube coordinates]
  (nth 
   (nth (get cube (:z coordinates) nil)
        (:y coordinates) nil)
   (:x coordinates) false))

(defn get-active-neighbor-count
  [cube coordinate]
  (count (filter #(active? cube %)
                 (get-all-neighbor-coordinates coordinate))))
  
(defn get-next-state-for-coordinate
  [cube coordinate]
  (let [active-neighbors (get-active-neighbor-count cube coordinate)]
    (or (= 3 active-neighbors) 
        (and (active? cube coordinate) (= 2 active-neighbors)))
    ))
  
(defn get-next-state-for-x
  [cube x-row y z]
  (mapv #(get-next-state-for-coordinate cube %)
       (map (fn [x] {:x x :y y :z z}) (range -1 (inc (count x-row)))))) ; check +/- 1

(defn empty-row
  [length]
  (into [] (repeat length false)))

(defn get-next-state-for-xy-plane
  [cube plane z]
  (let [plane-with-edge-rows (into [(empty-row (count (first plane)))] (conj plane (empty-row (count (first plane)))))]
    (map #(get-next-state-for-x cube %1 %2 z)
         plane-with-edge-rows
       ; need to check +/- 1 y to see if we need a new row
         (range -1 (inc (count (plane)))))))

(defn empty-plane
  [size]
  (into [] (repeat size (empty-row size))))

(defn get-next-state
  [cube]
  ; need to check +/- 1 for each edge to see if new things are generated
  (let [min_z (first (first (min-key first cube)))
        max_z (first (first (max-key first cube)))
        default-plane (empty-plane (count (second (first cube))))]
    (into {} 
          (map #(vector % (get-next-state-for-xy-plane cube (get cube % default-plane) %)) 
               (range (dec min_z) (+ 2 max_z))))))

(defn solve 
  ([] (solve (parse-input input)))
  ([original-xy-plane]
   (get-next-state {0 original-xy-plane})))

(defn -main
  "I don't do a whole lot ... yet."
  [& args]
  (println "Hello, World!"))
