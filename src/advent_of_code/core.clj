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

; have a sparse set that only contains coordinates of active cells
; TODO: current implementation depends on having at least an active cell at
; both min and max for x and y. Could easily change but too lazy rn.
(defn parse-row
  [y row] 
  (into #{} (keep-indexed (fn [x v] (if v {:x x :y y :z 0 :w 0})) (map #(= \# %) row))))

(defn parse-input
  "Returns a map
   where the key is the y coordinate
   and the value is a map from x coordinate to state"
  [in]
  (reduce #(into %1 %2) #{} (map-indexed parse-row (str/split in #"\n"))))

(def inc-x #(update % :x inc))
(def dec-x #(update % :x dec))
(def inc-y #(update % :y inc))
(def dec-y #(update % :y dec))
(def inc-z #(update % :z inc))
(def dec-z #(update % :z dec))
(def inc-w #(update % :w inc))
(def dec-w #(update % :w dec))

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
   (reduce reduce-to-set #{coordinate} [[inc-w dec-w identity]
                                        [inc-z dec-z identity]
                                        [inc-y dec-y identity]
                                        [inc-x dec-x identity]])
   coordinate))

(defn active?
  [cube coordinate]
  (contains? cube coordinate))

(defn get-active-neighbor-count
  [cube coordinate]
  (count (filter #(active? cube %)
                 (get-all-neighbor-coordinates coordinate))))
  
(defn next-state-is-active?
  [cube coordinate]
  (let [active-neighbors (get-active-neighbor-count cube coordinate)]
    (or (= 3 active-neighbors) 
        (and (active? cube coordinate) (= 2 active-neighbors)))
    ))

(def range-coordinates-with-edges #(range (dec %1) (+ 2 %2))) ;returns [min - 1, max + 1] to check edges

(defn get-next-state-for-x
  [cube limits y z w]
  (filter #(next-state-is-active? cube %)
          (map (fn [x] {:x x :y y :z z :w w})
               (range-coordinates-with-edges (:x (:mins limits)) (:x (:maxes limits)))))) 

(defn get-next-state-for-xy-plane
  [cube limits z w]
  (reduce #(into %1 %2) #{} (map #(get-next-state-for-x cube limits % z w)
       (range-coordinates-with-edges (:y (:mins limits)) (:y (:maxes limits))))))

(defn get-next-state-for-xyz-plane
  [cube limits w]
  (reduce #(into %1 %2) #{} (map #(get-next-state-for-xy-plane cube limits % w)
                                 (range-coordinates-with-edges (:y (:mins limits)) (:y (:maxes limits))))))

(defn get-next-state
  [cube limits]
  (reduce #(into %1 %2) #{} (map #(get-next-state-for-xyz-plane cube limits %)
                (range-coordinates-with-edges (:w (:mins limits)) (:w (:maxes limits))))))


(defn apply-to-coordinate
  [func cube coordinate]
  (apply func (map #(get % coordinate) cube)))

(defn min-coordinate
  [cube coordinate]
  (apply-to-coordinate min cube coordinate))

(defn max-coordinate
  [cube coordinate]
  (apply-to-coordinate max cube coordinate))

(defn get-coordinate-limits
  [cube]
  {:mins (into {} (map #(vector % (min-coordinate cube %)) [:z :y :x :w]))
   :maxes (into {} (map #(vector % (max-coordinate cube %)) [:z :y :x :w]))})

(defn apply-to-dimensions
  [coordinate f]
  (reduce-kv (fn [m k v] (assoc m k (f v))) {} coordinate))

; TODO: I wish I could use fmap (i.e., (use '[clojure.algo.generic.functor :only (fmap)]))
; here, but I can't figure out the import problems
(defn update-limits
  [limits]
  (update (update limits :mins #(apply-to-dimensions % dec))
          :maxes #(apply-to-dimensions % inc)))

(defn solve 
  ([] (solve (parse-input input)))
  ([original-xy-plane]
   (solve original-xy-plane (get-coordinate-limits original-xy-plane) 6))
  ([cube limits cycles]
   (if (= 0 cycles)
     (count cube)
     (solve (get-next-state cube limits) 
            (update-limits limits)
            (dec cycles)))))

(defn -main
  "I don't do a whole lot ... yet."
  [& args]
  (println "Hello, World!"))
