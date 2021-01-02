(ns advent-of-code.core
  (:gen-class))

(def input '(0, 6, 1, 7, 2, 19, 20))

(defn get-recent-turns-for-number
  [spoken n]
  (sort > (map first (filter #(= n (second %)) spoken))))

(defn add-turn
  [n turn spoken]
  (assoc spoken turn ; need to get the last TWO times the number was spoken
         (let [last-turns (get-recent-turns-for-number spoken n)]
           (if (< (count last-turns) 2)
             0
             (- (first last-turns) (second last-turns))))))

(defn find-spoken
  ([spoken max-turn] (find-spoken spoken (count spoken) max-turn))
  ([spoken turn max-turn]
   (let [last-spoken (get spoken (dec turn))]
     (if (= turn max-turn)
       last-spoken
       (find-spoken (add-turn last-spoken turn spoken) 
                    (inc turn) max-turn)))))

(defn solve
  ([] (solve input 2020))
  ([starting-numbers max-turn]
   (find-spoken (into {} (map-indexed #(vector %1 %2) starting-numbers)) max-turn)))


(defn -main
  "I don't do a whole lot ... yet."
  [& args]
  (println "Hello, World!"))
