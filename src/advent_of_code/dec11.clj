(ns advent-of-code.dec11)
(require '[clojure.java.io :as io])
(require '[clojure.string :as str])

(def input (slurp (.getFile (io/resource "dec11_input.txt"))))

; just a variant on Conway's game of life

(defn parse-row
  [row]
  (map #(case %
          \. :floor
          \L :empty
          \# :occupied)
       row))

(defn parse-input
  [in]
  (map parse-row (str/split in #"\n")))

(defn see-seat
  "y index is first, then x index, when accessing rows. If the indices are out of bounds, treat it like the floor"
  ([seats x y] (see-seat seats x y :floor))
  ([seats x y default]
   (if-let [row (nth seats y nil)]
     (nth row x default)
     default)))

(defn see-visible-seat
  [seats coords direction]
  (if-let [seat (see-seat seats (:x coords) (:y coords) nil)]
    (if (= :floor seat)
      (see-visible-seat seats (direction coords) direction)
      seat)
    :floor))

;directions
(def left #(update % :x dec))
(def right #(update % :x inc))
(def top #(update % :y dec))
(def bottom #(update % :y inc))

(defn num-visible-seats-occupied
  "As opposed to num-adjacent-occupied (see part 2 description).
   There's probably a way to share the code between these methods but leave that for another day."
  [seats coords]
  (count (filter #(= :occupied %)
                 (map #(see-visible-seat seats (% coords) %)
                      [#(top (left %))
                       top
                       #(top (right %))
                       left
                       right
                       #(bottom (left %))
                       bottom
                       #(bottom (right %))]))))

(defn get-next-state
  [seats x y]
  (let [current (see-seat seats x y)]
    (case current
      :empty (if (= 0 (num-visible-seats-occupied seats {:x x, :y y})) :occupied :empty)
      :occupied (if (< 4 (num-visible-seats-occupied seats {:x x, :y y})) :empty :occupied)
      current)))

; only use vectors with mapv or else the solution takes too long 
; (doing index lookup in lists isn't fast).
; I wonder if I could've avoided indices altogether...

(defn get-next-row
  [seats current-y]
  (mapv #(get-next-state seats % current-y) (range (count (nth seats current-y)))))

(defn get-next-seats
  [seats]
  (mapv #(get-next-row seats %) (range (count seats))))

(defn count-occupied-in-row
  [row]
  (count (filterv #(= :occupied %) row)))

(defn count-occupied-seats
  [seats]
  (reduce + (mapv count-occupied-in-row seats)))

(defn find-equilibrium-count
  ([] (find-equilibrium-count (vec (map vec (parse-input input)))))
  ([seats]
   (let [next-seats (get-next-seats seats)]
     (if (= seats next-seats)
       (count-occupied-seats seats)
       (find-equilibrium-count next-seats)))))


; functions to help debug

(defn seat-to-str
  [s]
  (case s
    :floor "."
    :empty "L"
    :occupied "#"))

(defn print-row
  [row]
  (println (str/join "" (map seat-to-str row))))

(defn print-seats
  [seats]
  (map print-row seats))