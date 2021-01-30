(ns advent-of-code.dec17-test)
; TODO: don't know how to properly reference the code in other files so...
;; 
  ;; (:require [clojure.test :refer :all]
            ;; [advent-of-code.dec17 :refer [get-all-neighbor-coordinates solve]]))

;; (def neighbors-of-origin
;;   #{; first 8 at z = 0
;;     {:x -1 :y -1 :z 0}
;;     {:x 0 :y -1 :z 0}
;;     {:x 1 :y -1 :z 0}
;;     {:x -1 :y 0 :z 0}
;;     {:x 1 :y 0 :z 0}
;;     {:x -1 :y 1 :z 0}
;;     {:x 0 :y 1 :z 0}
;;     {:x 1 :y 1 :z 0}

;;     ; next 9 at z = -1
;;     {:x -1 :y -1 :z -1}
;;     {:x 0 :y -1 :z -1}
;;     {:x 1 :y -1 :z -1}
;;     {:x -1 :y 0 :z -1}
;;     {:x 1 :y 0 :z -1}
;;     {:x -1 :y 1 :z -1}
;;     {:x 0 :y 1 :z -1}
;;     {:x 1 :y 1 :z -1}
;;     {:x 0 :y 0 :z -1}

;;     ; last 9 at z = 1
;;     {:x -1 :y -1 :z 1}
;;     {:x 0 :y -1 :z 1}
;;     {:x 1 :y -1 :z 1}
;;     {:x -1 :y 0 :z 1}
;;     {:x 1 :y 0 :z 1}
;;     {:x -1 :y 1 :z 1}
;;     {:x 0 :y 1 :z 1}
;;     {:x 1 :y 1 :z 1}
;;     {:x 0 :y 0 :z 1}
;;     })

;; (deftest test-get-all-neighbor-coordinates
;;   (testing "Make sure it gets all 26 coordinates"
;;     (is (= neighbors-of-origin 
;;            (get-all-neighbor-coordinates {:x 0 :y 0 :z 0})))))

;; (deftest part1
;;   (testing "So I can refactor the part 1 code and make sure it still works"
;;     (is (= 336 (solve)))))