(ns advent-of-code.dec20-test)
    ;; (:require [clojure.test :refer :all]
            ;; [advent-of-code.core :refer :all]))

;; 
;; ; TODO: don't know how to properly reference the code in other files so...

;; (defn get-max
;;   [coords axis]
;;   (axis (first (apply max-key #(axis (key %)) coords))))

;; (defn piece-with-id-maxes
;;   ([id coords max-x max-y] [id {:piece coords :max-x max-x :max-y max-y}])
;;   ([id coords] (piece-with-id-maxes id coords (get-max coords :x) (get-max coords :y)))
;;   ([coords] (piece-with-id-maxes 1234 coords)))

;; (defn piece-with-id
;;   ([id coords] [id {:piece coords}])
;;   ([coords] (piece-with-id 1234 coords)))

;; (deftest test-get-top-edge
;;   (testing "Get top edge empty"
;;     (is (= {} (get-top-edge {:piece {}}))))
;;   (testing "Get top edge all y=0"
;;     (is (= {{:x 1 :y 0} true {:x 4 :y 0} false}
;;            (get-top-edge {:piece {{:x 1 :y 0} true
;;                                   {:x 4 :y 0} false}}))))
;;     (testing "Get top edge mixed"
;;       (is (= {{:x 1 :y 0} true {:x 4 :y 0} false}
;;              (get-top-edge {:piece {{:x 1 :y 0} true
;;                                     {:x 4 :y 0} false
;;                                     {:x 34 :y 7} false
;;                                     {:x 65 :y 1} true}})))))

;; (deftest test-do-top-edges-match?
;;   (testing "Empty pieces"
;;     (is (do-top-edges-match? (piece-with-id {}) (piece-with-id {}))))
;;   (testing "Match returns id of second piece"
;;     (is (= 2 (do-top-edges-match? (piece-with-id 1 {}) (piece-with-id 2 {})))))
;;   (testing "Pieces with disjoint coordinates don't match"
;;     (is (not (do-top-edges-match? (piece-with-id {{:x 1 :y 0} false}) 
;;                                   (piece-with-id {{:x 2 :y 0} false})))))
;;   (testing "Pieces with different values don't match"
;;     (is (not (do-top-edges-match? (piece-with-id {{:x 1 :y 0} false}) 
;;                                   (piece-with-id {{:x 1 :y 0} true})))))
;;   (testing "Pieces match"
;;     (is (do-top-edges-match? (piece-with-id {{:x 1 :y 0} false}) 
;;                              (piece-with-id {{:x 1 :y 0} false})))))


;; (deftest test-matches-top-edge?
;;   (testing "Empty pieces"
;;     (is (matches-top-edge? (piece-with-id {}) (piece-with-id {}))))
;;   (testing "Match returns id of second piece"
;;     (is (= 2 (matches-top-edge? (piece-with-id 1 {}) (piece-with-id 2 {})))))
;;   (testing "Match same top edge"
;;     (is (matches-top-edge? (piece-with-id-maxes {{:x 0 :y 0} true}) 
;;                            (piece-with-id-maxes {{:x 0 :y 0} true}))))
;;   (testing "Match same top edge, flipped"
;;     (is (matches-top-edge? (piece-with-id-maxes {{:x 0 :y 0} true {:x 1 :y 0} false})
;;                            (piece-with-id-maxes {{:x 0 :y 0} false {:x 1 :y 0} true}))))
;;   (testing "Match same top edge, rotated"
;;     (is (matches-top-edge? (piece-with-id-maxes {{:x 0 :y 0} true})
;;                            (piece-with-id-maxes {{:x 0 :y 1} true}))))
;;   (testing "Match same top edge, rotated and flipped"
;;     (is (matches-top-edge? (piece-with-id-maxes {{:x 0 :y 0} true {:x 1 :y 0} false})
;;                            (piece-with-id-maxes {{:x 0 :y 1} false {:x 1 :y 1} true})))))

;; (deftest test-pieces-match?
;;   (testing "Empty pieces"
;;     (is (pieces-match? (piece-with-id 1 {}) (piece-with-id 2 {}))))
;;   (testing "Pieces with same ID don't match"
;;     (is (not (pieces-match? (piece-with-id 1 {}) (piece-with-id 1 {})))))
;;   (testing "Match returns ID of second piece"
;;     (is (= 2 (pieces-match? (piece-with-id 1 {}) (piece-with-id 2 {})))))
;;   (testing "Bottom edge of piece 1 matches top of piece 2"
;;     (is (pieces-match? (piece-with-id-maxes 1 {{:x 0 :y 4} true {:x 1 :y 4} false})
;;                        (piece-with-id-maxes 2 {{:x 0 :y 0} true {:x 1 :y 0} false
;;                                                {:x 1 :y 4} true})))) ; add one coordinate with y=4 for proper translations
;;   (testing "Left edge of piece 1 matches right of piece 2"
;;     (is (pieces-match? (piece-with-id-maxes 1 {{:x 0 :y 0} true {:x 0 :y 1} true {:x 0 :y 2} true
;;                                                {:x 2 :y 1} false}) ; add x=2 for proper translations
;;                        (piece-with-id-maxes 2 {{:x 2 :y 0} true {:x 2 :y 1} true {:x 2 :y 2} true}))))
;;   (testing "Pieces don't match"
;;     (is (not (pieces-match? (piece-with-id-maxes 1 {{:x 0 :y 0} true {:x 0 :y 1} true {:x 0 :y 2} true
;;                                                     {:x 2 :y 1} false}) ; add x=2 for proper translations
;;                             (piece-with-id-maxes 2 {{:x 2 :y 0} false {:x 2 :y 1} false {:x 2 :y 2} false}))))))

;; (deftest test-get-piece-matches
;;   (testing "Piece does not match with no pieces"
;;     (is (= '() (get-piece-matches (piece-with-id {}) '()))))
;;   (testing "Empty pieces match"
;;     (is (= '(2 3) (get-piece-matches (piece-with-id 1 {}) 
;;                                   (list (piece-with-id 2 {}) (piece-with-id 3 {})))))))

;; (deftest test-remove-edges
;;   (testing "Get center updates coordinates and maxes"
;;     (is (= (piece-with-id-maxes 1 {{:x 0 :y 0} false} 0 0) 
;;            (remove-edges (piece-with-id-maxes 1 {{:x 0 :y 0} true
;;                                                {:x 1 :y 1} false
;;                                                {:x 2 :y 2} true} 2 2))))))

;; (deftest test-piece-to-string
;;   (testing "One row"
;;     (is (= ".##" (piece-to-string (piece-with-id-maxes {{:x 0 :y 0} false {:x 1 :y 0} true {:x 2 :y 0} true})))))
;;   (testing "One column"
;;     (is (= ".\n#\n#" (piece-to-string (piece-with-id-maxes {{:x 0 :y 0} false 
;;                                                               {:x 0 :y 1} true 
;;                                                               {:x 0 :y 2} true})))))
;;   (testing "Piece"
;;     (is (= ".##\n#.#" (piece-to-string (piece-with-id-maxes {{:x 0 :y 0} false {:x 1 :y 0} true {:x 2 :y 0} true
;;                                                                {:x 0 :y 1} true {:x 1 :y 1} false {:x 2 :y 1} true}))))))
