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
  (into {} (map parse-tile (str/split in #"\n\n"))))

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
  ([piece-with-id f]
   (transform-piece-with-id piece-with-id f identity))
  ([piece-with-id f new-max-f]
      [(first piece-with-id)
       (let [piece (second piece-with-id)]
         {:piece (into {} (keep f (:piece piece)))
          :max-x (new-max-f (:max-x piece))
          :max-y (new-max-f (:max-y piece))})]))

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

(defn update-coordinate
  [coord axis f]
  [(update (first coord) axis f)
   (second coord)])

(defn flip-x
  [coordinate max-x]
  (update-coordinate coordinate :x #(- max-x %)))

(defn flip
  "Only need to rotate around the y-axis, and all of the rotations will handle the rest."
  [piece-with-id]
  (transform-piece-with-id piece-with-id #(flip-x % (:max-x (second piece-with-id)))))

(defn get-top-edge
  [piece]
  ;get all coordinates for y=0
  (into {} (filter #(= 0 (:y (first %))) (:piece piece))))

(defn do-top-edges-match?
  "If the two top edges match, return the second piece"
  [piece1-with-id piece2-with-id]
  (if (= (get-top-edge (second piece1-with-id))
         (get-top-edge (second piece2-with-id)))
    piece2-with-id
    nil))

(defn matches-top-edge?
  "Returns the piece ID of the second piece"
  [piece1-with-id piece2-with-id]
  (let [rotations (get-all-rotations piece2-with-id)]
    (some #(p :do-top-edges-match? (do-top-edges-match? piece1-with-id %)) (concat rotations (map flip rotations)))))

(defn matches-right-edge?
  [piece1-with-id piece2-with-id]
  ; This rotates the first piece to get the edge,
  ; then we need to rotate (clockwise) the resulting match to get the proper orientation
  (rotate (matches-top-edge? (rotate piece1-with-id) piece2-with-id)))

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
  (map first (keep #(p :pieces-match? (pieces-match? piece-with-id %)) all-pieces-with-ids)))

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

(defn remove-edges
  [piece-with-id]
  ; need to decrease each coordinate by 1 (and the maxes by 2)
  (transform-piece-with-id piece-with-id #(if (not (is-edge? % (:max-x (second piece-with-id)) (:max-y (second piece-with-id))))
                                            (update-coordinate (update-coordinate % :x dec) :y dec)
                                            nil) #(- % 2)))

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
   (profile {} (reduce * (map first (filter #(= 2 (count (second %))) (p :find-piece-matches (find-piece-matches all-pieces))))))))

; For part 2, take results from part 1 (to know what matches where)
; A monster spans a total of a 20x3 space. 
; Because we know our pieces have no edge, the max goes down from 9 to 7 (size is 8).
; So we need to look at min. 3 pieces at a time 
; And max of 6 pieces (if it is right at the top/bottom edge)
; Also it could be in any rotation or flipped. Maybe it's easier if I put together all of the pieces, and then look for sea monsters
; It would almost be easier to put the puzzle together and combine them all back into strings...
; 
; Overall, I'm looking for all the `#` that don't belong to a sea monster
; 

; This sounds hard..

; If I were to combine them into a large puzzle, how would I do so?
; Well, I could do it like a puzzle. 
; First, find a corner piece, then connect it all along the top edge.
; Then go through all the remaining pieces a make a row for each of them.
; We know to go on to the next row either i) once we see another edge piece or 
; ii) we hit the length of the top row (seems easier since we know the length after reaching the top corner)
; Then, after we've connected all of the pieces, remove all the edges.
; And combine into strings
; 
; Is there an easy way to do this with the output of part 1?
; I could find a corner, and then just build pieces on top of it (i.e., we know there will be one piece that matches one of the top sides)
; And then just keep building pieces on top until I hit the corner, and keep going
; 
; So visually it looks like this:
; 
;                               #.#
; Step 1:                       ### 
;                               ...
;
;
; Step 2: (with next piece)     .#.
;                               .#.
;                               #.#
;                               
;                               #.#
;                               ###
;                               ...
;                               
; and then just keep building on top of that edge, until there are no more.
; This becomes WAY easier than searching through all of the pieces. Since 
; we already know what pieces match, we can just look at 4 pieces (max) at a time.
; 
; But how can I store this relationship?
; Should I put them in strings as I build them? Might be easier..
; Orientation isn't that big of a deal (just as long as pieces match). And should be fine if I do it per-column
; 

(defn row-to-string
  [piece y-index]
  (str/join (map #(if % "#" ".") (map second (sort-by #(:x (first %))
                                                       (filter #(= y-index (:y (first %))) piece))))))
(defn piece-to-string
  [piece-with-id]
  (let [piece (second piece-with-id)]
    (str/join "\n" (map #(row-to-string (:piece piece) %) (range (inc (:max-y piece)))))))


(defn find-matching-piece
  [all-pieces piece-matches piece-with-id f]
  (some #(f piece-with-id (get all-pieces %)) (get piece-matches (first piece-with-id))))

(defn build-column
  ([all-pieces piece-matches column]
   (if-let [next-piece (find-matching-piece all-pieces piece-matches (first column) matches-top-edge?)]
     (build-column all-pieces piece-matches (cons next-piece column))
     column)))

(defn puzzle-full?
  [all-pieces-count columns]
  ; number of columns * pieces/columns
  (= all-pieces-count (* (count columns) (count (first columns)))))

(defn find-bottom-left-corner
  "Need to find a corner piece, 
   and orient it such that it can be the bottom left of the puzzle"
  [all-pieces piece-matches]
  (let [corner-id (first (first (filter #(= 2 (count (second %))) piece-matches)))]
    (first (filter #(find-matching-piece all-pieces piece-matches % matches-right-edge?)
                    (filter #(find-matching-piece all-pieces piece-matches % matches-top-edge?)
                            (get-all-rotations [corner-id (get all-pieces corner-id)]))))))

(defn find-piece-matching-right-edge
  [all-pieces piece-matches piece-with-id]
  (find-matching-piece all-pieces piece-matches piece-with-id matches-right-edge?))

(defn build-puzzle
  ([all-pieces piece-matches] 
     (build-puzzle all-pieces piece-matches (list (build-column all-pieces piece-matches (list (find-bottom-left-corner all-pieces piece-matches))))))
  ([all-pieces piece-matches columns]   
   (if (puzzle-full? (count all-pieces) columns)
     columns
     (conj columns (build-column all-pieces piece-matches
                                 (find-piece-matching-right-edge all-pieces piece-matches (last (first columns))))))))


; TODO: then need to combine to string, then can start search.
(defn solve-part2
  ([] (parse-input input))
  ([all-pieces]
  (build-puzzle all-pieces (find-piece-matches all-pieces))))

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
(def ex-matches (find-piece-matches ex-all))

; One thing I noticed, is that we may have to specailly account for flips as well.
; For example, if there is a piece with a symetrical edge, then we need to find the correct 
; orientation so orthogonal pieces match up as well.
; 
; For a piece with an edge length of 10, that means that there are 2^(floor(10 / 2)) = 32 symmetrical edges
; It could be possible that there are no pieces with symmetrical edges, because there
; are a total of 2^10 = 1024 edges. Because we have 144 pieces (so 576 edges), it's totally within reason.
; Let's check to make sure (and make our code easier).
; 
; It works, so we know that if we find a piece that matches with one edge, then that is the ONLY orientation
; that the piece can match.

(defn is-top-edge-symmetrical?
  [piece-with-id]
  ; if flipped edge matches, then symmetrical
 (do-top-edges-match? piece-with-id (flip piece-with-id)))
(defn has-symmetrical-edge? [piece-with-id] (some is-top-edge-symmetrical? (get-all-rotations piece-with-id)))
(defn any-symmetrical-pieces? [all-pieces] (some has-symmetrical-edge? all-pieces))

(defn -main
  "I don't do a whole lot ... yet."
  [& args]
  (println "Hello, World!"))
