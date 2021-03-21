(ns advent-of-code.core
  (:gen-class))
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

; For each ingredient,
;    get the intersection of all recipes to see which ingredients are always present

(defn get-allergy-ingredient-candidates
  [allergens]
  (map #(apply clojure.set/intersection (second %)) allergens))

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
    (reduce clojure.set/union (get-allergy-ingredient-candidates allergens)))))

(defn -main
  "I don't do a whole lot ... yet."
  [& args]
  (println "Hello, World!"))
