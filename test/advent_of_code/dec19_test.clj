(ns advent-of-code.dec19-test)
;;   (:require [clojure.test :refer :all]
            ;; [advent-of-code.core :refer :all]))
;; 
;; ; TODO: don't know how to properly reference the code in other files so...

;; (def example-rules
;;   {0 '((4 1 5))
;;    1 '((2 3) (3 2))
;;    2 '((4 4) (5 5))
;;    3 '((4 5) (5 4))
;;    4 '((\a))
;;    5 '((\b))})

;; (deftest test-expand-rules
;;   (testing "0 maps to a"
;;     (is (= \a (expand-rules {0 '((\a))}))))
;;   (testing "0 maps to 1 to a"
;;     (is (= '((\a)) (expand-rules {0 '((1)) 1 '((\a))}))))
;;   (testing "0 maps to 1 and 2"
;;     (is (= '((\a \b)) (expand-rules {0 '((1 2)) 1 '((\a)) 2 '((\b))}))))
;;   (testing "0 maps to 1 OR 2"
;;     (is (= '((\a) (\b)) (expand-rules {0 '((1) (2)) 1 '((\a)) 2 '((\b))}))))
;;   (testing "Complex example"
;;     (is (= '((\a ; 4
;;              ((((\a \a) (\b \b)) ((\a \b) (\b \a))) ; 2 3
;;               (((\a \b) (\b \a)) ((\a \a) (\b \b)))) ; 3 2 (all in 1)
;;              \b))     ; 5
;;            (expand-rules example-rules)))))

;; (deftest test-combine-possibilities
;;   (testing "Get from single literal"
;;     (is (= #{"a"} (combine '((\a))))))
;;   (testing "Get from two literals"
;;     (is (= #{"ab"} (combine '((\a \b))))))
;;   (testing "Get from two options"
;;     (is (= #{"a" "b"} (combine '((\a) (\b))))))
;;   (testing "Get from two options"
;;     (is (= #{"ab" "aa"} (combine '((\a ((\b) (\a))))))))
;;   )