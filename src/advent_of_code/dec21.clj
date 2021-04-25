(ns advent-of-code.dec21)
(require '[clojure.java.io :as io])
(require '[clojure.string :as str])
(require '[clojure.set])

(def input (slurp (.getFile (io/resource "dec21_input.txt"))))


(defn parse-allergens
  [in]
  (str/split in #", "))

(defn parse-ingredients
  [in]
  (str/split in #" "))

; creates a map that goes:
;  single-allergen -> list of set of ingredients
(defn parse-line
  [line]
  (let [a (str/split line #" \(contains |\)")
        ingredients (set (parse-ingredients (first a)))]
    (into {} (map #(vector % (list ingredients))
                  (parse-allergens (second a))))))

; creates a map that goes:
;  single-allergen -> list of sets of ingredients (i.e., recipes) that contain that allergen
(defn parse-input
  [in]
  (apply (partial merge-with concat)
         (map parse-line (str/split-lines in))))

(defn parse-all-recipes
  "Get the ingredients for every recipe (but exclude any allegy info)"
  [in]
  (flatten (map #(first (vals (parse-line %))) (str/split-lines in))))

(defn map-vals
  [f map-entries]
  (into {} (map #(vector (first %) (f (second %))) map-entries)))

; For each ingredient,
;    get the intersection of all recipes to see which ingredients are always present

(defn get-allergy-ingredient-candidates
  [allergens]
  (map-vals #(apply clojure.set/intersection %) allergens))

(defn flatten-all-recipes
  [recipes]
  (reduce into '() (flatten recipes)))

(defn get-count-of-all-non-allergy-ingredients
  [recipes possible-allergy-ingredients]
  (count (filter #(not (contains? possible-allergy-ingredients %)) (flatten-all-recipes recipes))))

(defn solve-part-1
  ([] (solve-part-1 (parse-input input) (parse-all-recipes input)))
  ([allergens recipes]
   (get-count-of-all-non-allergy-ingredients
    recipes
    (reduce clojure.set/union (vals (get-allergy-ingredient-candidates allergens))))))

(defn find-allergy-ingredient
  [possible-allergy-ingredients]
  (map-vals first
            (filter #(= 1 (count (second %))) possible-allergy-ingredients)))

(defn remove-allergy-ingredient
  [possible-allergy-ingredients ingredients-to-remove]
  (map-vals #(apply (partial disj %) ingredients-to-remove) possible-allergy-ingredients))

(defn find-allergy-ingredients
  ([allergy-ingredient-candidates] (find-allergy-ingredients allergy-ingredient-candidates {}))
  ([allergy-ingredient-candidates total]
   (let [ingredients (find-allergy-ingredient allergy-ingredient-candidates)]
     (if (empty? ingredients)
       total
       (find-allergy-ingredients (remove-allergy-ingredient allergy-ingredient-candidates (vals ingredients)) (into total ingredients))))))

(defn solve-part-2
  ([] (solve-part-2 (parse-input input)))
  ([allergens]
   (str/join "," (vals (into (sorted-map)
                             (find-allergy-ingredients (get-allergy-ingredient-candidates allergens)))))))

