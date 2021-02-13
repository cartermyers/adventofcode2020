(ns advent-of-code.core
  (:gen-class))
(require '[clojure.java.io :as io])
(require '[clojure.string :as str])
(require '[taoensso.tufte :as tufte :refer (defnp p profiled profile)])

(def input (slurp (.getFile (io/resource "dec20_input.txt"))))

(defn parse-piece
  "Returns a map, where the key is the x,y coordinates and the value is the puzzle thing"
  [rows]
  {:piece (reduce into {} (map-indexed (fn [y r] (map-indexed #(vector {:x %1 :y y} (= \# %2)) r)) rows))
   :max-y (dec (count rows))
   :max-x (dec (count (first rows)))})

(defn parse-id
  [id]
  (Integer/parseInt (second (str/split id #"\s|:"))))

(defn parse-tile
  [tile]
  (let [p (str/split tile #"\n")]
    [(parse-id (first p))
     (parse-piece (rest p))]))

(defn parse-input
  [in]
  (map parse-tile (str/split in #"\n\n")))

;; end of parsing

(defn rotate-coordinate
  "Rotates a coordinate counterclockwise by 90 degrees
   Does so by translating the origin to the center of the piece,
   then translates the pieces back."
  [coordinate max-x max-y]
  [(let [translated-x (- (:x (first coordinate)) (/ max-x 2))
         translated-y (+ (- (:y (first coordinate))) (/ max-y 2))]
     {:x (Math/round (+ (/ max-x 2)
                         (- (* translated-x (Math/cos (/ Math/PI 2)))
                            (* translated-y (Math/sin (/ Math/PI 2))))))
      :y (Math/round (+ (/ max-y 2)
                         (- (+ (* translated-x (Math/sin (/ Math/PI 2)))
                               (* translated-y (Math/cos (/ Math/PI 2)))))))})
   (second coordinate)])

(defn transform-piece-with-id
  "Helper function for things like roatate and fil
   because they want to modify the piece but use max-x and max-y 
   (which) should never change"
  [piece-with-id f]
  [(first piece-with-id)
   (let [piece (second piece-with-id)]
     {:piece (into {} (keep f (:piece piece)))
      :max-x (:max-x piece)
      :max-y (:max-y piece)})])

(defn rotate
  [piece-with-id]
  (p :rotate (let [piece (second piece-with-id)]
                (transform-piece-with-id piece-with-id #(rotate-coordinate % (:max-x piece) (:max-y piece))))))

(defn get-all-rotations
  "For a given piece, returns a list of that piece rotated 90 degrees at a time."
  [piece-with-id]
  (p :get-all-rotations (loop [count 0
             res (list piece-with-id)]
        (if (>= count 3)
          res
          (recur (inc count)
                 (conj res (rotate (first res))))))))

(defn flip-x
  [coordinate max-x]
  [(update (first coordinate) :x #(- max-x %))
   (second coordinate)])

(defn flip
  "Only need to rotate around the y-axis, and all of the rotations will handle the rest."
  [piece-with-id]
  (transform-piece-with-id piece-with-id #(flip-x % (:max-x (second piece-with-id)))))

(defn get-top-edge
  [piece]
  ;get all coordinates for y=0
  (into {} (filter #(= 0 (:y (first %))) (:piece piece))))

(defn do-top-edges-match?
  "If the two top edges match, return the piece ID of the second piece only"
  [piece1-with-id piece2-with-id]
  (if (= (get-top-edge (second piece1-with-id))
         (get-top-edge (second piece2-with-id)))
    (first piece2-with-id)
    nil))

(defn matches-top-edge?
  "Returns the piece ID of the second arguement if any edge of piece2 matches the top of piece1"
  [piece1-with-id piece2-with-id]
    (let [rotations (get-all-rotations piece2-with-id)]
      (some #(p :do-top-edges-match? (do-top-edges-match? piece1-with-id %)) (concat rotations (map flip rotations))))
  )

(tufte/add-basic-println-handler! {})

(defn pieces-match?
  "For these two pieces, we need to find two edges that match.
   Given an edge of piece1, any edge in piece2 can match (if flipped or rotated)
   And then it would need to be rotated"
  [piece1-with-id piece2-with-id]
  (if (= (first piece1-with-id) (first piece2-with-id)) ; we won't let pieces match themselves. if they have the same id, then it's nothing
    nil
    (some #(p :matches-top-edge? (matches-top-edge? % piece2-with-id)) (get-all-rotations piece1-with-id))))

(defn get-piece-matches
  "Currently takes full O(n^2) because I check all of the pieces in the bag.
   We could stop once a piece has 4 edges. And if we account for all its partners, 
   then we can even remove that piece from future checks.
   But I think this is not needed yet."
  [piece-with-id all-pieces-with-ids]
  (keep #(p :pieces-match? (pieces-match? piece-with-id %)) all-pieces-with-ids))

(defn is-edge?
  [val max-x max-y]
  (let [coordinate (first val)]
    (if (or (= 0 (:x coordinate))
            (= 0 (:y coordinate))
            (= max-x (:x coordinate))
            (= max-y (:y coordinate)))
      val
      nil)))

(defn remove-insides
  [piece-with-id]
  (transform-piece-with-id piece-with-id #(is-edge? % (:max-x (second piece-with-id)) (:max-y (second piece-with-id)))))

(defn find-piece-matches
  ([all-pieces]
   (let [pieces-with-only-edges (map remove-insides all-pieces)]
     (into {} (map #(vector (first %) (p :get-piece-matches (get-piece-matches % pieces-with-only-edges))) pieces-with-only-edges)))))

; we only need to find the edges, so those are 4 pieces that only have 2 matching pieces in the bag.
; hopefully there aren't multiple matches (i.e., a single piece can work for more than 4 pieces or something)
; that would be a shitty puzzle

(defn solve-part1
  ([] (solve-part1 (parse-input input)))
  ([all-pieces] 
   (reduce * (map first (filter #(= 2 (count (second %))) (find-piece-matches all-pieces))))))


(def ex "Tile 2311:
..##.#..#.
##..#.....
#...##..#.
####.#...#
##.##.###.
##...#.###
.#.#.#..##
..#....#..
###...#.#.
..###..###

Tile 1951:
#.##...##.
#.####...#
.....#..##
#...######
.##.#....#
.###.#####
###.##.##.
.###....#.
..#.#..#.#
#...##.#..

Tile 1171:
####...##.
#..##.#..#
##.#..#.#.
.###.####.
..###.####
.##....##.
.#...####.
#.##.####.
####..#...
.....##...

Tile 1427:
###.##.#..
.#..#.##..
.#.##.#..#
#.#.#.##.#
....#...##
...##..##.
...#.#####
.#.####.#.
..#..###.#
..##.#..#.

Tile 1489:
##.#.#....
..##...#..
.##..##...
..#...#...
#####...#.
#..#.#.#.#
...#.#.#..
##.#...##.
..##.##.##
###.##.#..

Tile 2473:
#....####.
#..#.##...
#.##..#...
######.#.#
.#...#.#.#
.#########
.###.#..#.
########.#
##...##.#.
..###.#.#.

Tile 2971:
..#.#....#
#...###...
#.#.###...
##.##..#..
.#####..##
.#..####.#
#..#.#..#.
..####.###
..#.#.###.
...#.#.#.#

Tile 2729:
...#.#.#.#
####.#....
..#.#.....
....#..#.#
.##..##.#.
.#.####...
####.#.#..
##.####...
##..#.##..
#.##...##.

Tile 3079:
#.#.#####.
.#..######
..#.......
######....
####.#..#.
.#...#.##.
#.#####.##
..#.###...
..#.......
..#.###...")

(def ex-all (parse-input ex))

(defn -main
  "I don't do a whole lot ... yet."
  [& args]
  (println "Hello, World!"))
