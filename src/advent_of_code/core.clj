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
  [row] 
  (into {} (map-indexed vector (map #(= \# %) row))))

(defn parse-input
  "Returns a map
   where the key is the y coordinate
   and the value is a map from x coordinate to state"
  [in]
  (into {} (map-indexed vector (map parse-row (str/split in #"\n")))))

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
  (get 
   (get (get cube (:z coordinates) nil)
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
  
(def min-coordinate (memoize #(first (apply min-key first %)))) ; TODO: memoize may not be needed here
(def max-coordinate (memoize #(first (apply max-key first %))))
(def range-coordinates-with-edges #(range (dec %1) (+ 2 %2))) ;returns [min - 1, max + 1] to check edges

; TODO: also need to see if edges are empty.
; If they are, omit them. Else, add them.
; BUT we should leave in empty rows/planes that are in the middle
; Just for a little bit of efficiency in memory
(defn get-next-state-for-x
  [cube y z]
  {y (into {}
           (map #(vector % (get-next-state-for-coordinate cube {:x % :y y :z z})) 
                (range-coordinates-with-edges (:x (:mins cube)) (:x (:maxes cube)))))}) 

(defn get-next-state-for-xy-plane
  [cube z]
  {z (into {}
           (map #(get-next-state-for-x cube % z)
                (range-coordinates-with-edges (:y (:mins cube)) (:y (:maxes cube)))))})

; TODO: I could also just dec/inc the old min/max without iterating through the whole map...
(defn get-cube-with-mins-and-maxes
  [cube]
  (into cube {:mins {:z (min-coordinate cube) :y (min-coordinate (second (first cube))) :x (min-coordinate (second (first (second (first cube)))))}
              :maxes {:z (max-coordinate cube) :y (max-coordinate (second (first cube))) :x (max-coordinate (second (first (second (first cube)))))}}))

; TODO: there's a lot of similar structure with this function
; and get-next-state-for-xy-plane and get-next-state-for-x.
; I'm sure I could consolidate them, but another day.
(defn get-next-state
  [cube]
(let [new-cube 
      (into {} (map #(get-next-state-for-xy-plane cube %)
                    (range-coordinates-with-edges (:z (:mins cube)) (:z (:maxes cube)))))]
  (get-cube-with-mins-and-maxes new-cube)))

(defn count-row
  [row]
  (count (filter second row)))

(defn count-plane
  [plane]
  (reduce + (map #(count-row (second %)) plane)))

(defn count-active
  [cube]
  (reduce + (map #(count-plane (second %)) (dissoc cube :mins :maxes))))

(defn solve 
  ([] (solve (parse-input input)))
  ([original-xy-plane]
   (solve (get-cube-with-mins-and-maxes {0 original-xy-plane}) 6))
  ([cube cycles]
   (if (= 0 cycles)
     (count-active cube)
     (solve (get-next-state cube) (dec cycles)))))

(defn row-to-str
  [row]
  (str/join (map #(if (second %) "#" ".") row)))

(defn plane-to-str
  [plane]
  (str/join "\n" (map #(str "y=" (first %) " " (row-to-str (second %))) plane)))

(defn print-cube
  [cube]
  (doseq [a (map #(vector (first %) (plane-to-str (second %))) 
                 (dissoc cube :mins :maxes))]
    (println "z=" (first a) "\n" (second a) "\n")
    ))

(defn -main
  "I don't do a whole lot ... yet."
  [& args]
  (println "Hello, World!"))
