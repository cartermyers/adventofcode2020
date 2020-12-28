(ns advent-of-code.core
  (:gen-class))
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
  [seats x y]
  ;(memoize (fn [seats x y]
             (if-let [row (nth seats y nil)]
               (nth row x :floor)
               :floor))
;))

(defn num-adjacent-occupied
  [seats x y]
    (count (filter #(= :occupied %)
                   (map #(see-seat seats (first %) (second %))
                        [[(dec x) (dec y)]
                         [x (dec y)]
                         [(inc x) (dec y)]
                         [(dec x) y]
                         [(inc x) y]
                         [(dec x) (inc y)]
                         [x (inc y)]
                         [(inc x) (inc y)]]))))

(defn get-next-state
  [seats x y]
  (let [current (see-seat seats x y)]
    (case current
      :empty (if (= 0 (num-adjacent-occupied seats x y)) :occupied :empty)
      :occupied (if (< 3 (num-adjacent-occupied seats x y)) :empty :occupied)
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

(defn -main
  "I don't do a whole lot ... yet."
  [& args]
  (println "Hello, World!"))
