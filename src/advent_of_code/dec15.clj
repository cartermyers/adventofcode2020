(ns advent-of-code.dec15)
(def input '(0, 6, 1, 7, 2, 19, 20))

(defn find-spoken
  ([spoken last-spoken max-turn] (find-spoken spoken last-spoken (count spoken) max-turn))
  ([s ls lt max-turn]
   (loop [spoken s
          last-spoken ls
          last-turn lt]
     (if (= (inc last-turn) max-turn)
       last-spoken
       (recur (assoc spoken last-spoken last-turn)
              (- last-turn (get spoken last-spoken last-turn))
              (inc last-turn))))))

(defn solve
  ([] (solve 2020))
  ([max-turn] (solve input max-turn))
  ([starting-numbers max-turn]
   (let [last-spoken (last starting-numbers)]
     (find-spoken (dissoc
                   (into {} (map-indexed #(vector %2 %1) starting-numbers)) ;remove last spoken number from map
                   last-spoken)
                  last-spoken max-turn))))
