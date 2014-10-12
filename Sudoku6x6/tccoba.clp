(defrule grid-values

   ?f <- (phase grid-values)

   =>
   
   (retract ?f)
   
   (assert (phase expand-any))

   (assert (size 5))
   
(assert (possible (row 1) (column 1) (group 1) (diagonal 1) (id 1) (value any)))
(assert (possible (row 1) (column 2) (group 1) (diagonal 3) (id 2) (value any)))
(assert (possible (row 1) (column 3) (group 1) (diagonal 3) (id 3) (value any)))
(assert (possible (row 2) (column 1) (group 1) (diagonal 3) (id 4) (value any)))
(assert (possible (row 2) (column 2) (group 1) (diagonal 1) (id 5) (value any)))
(assert (possible (row 2) (column 3) (group 1) (diagonal 3) (id 6) (value any)))
(assert (possible (row 1) (column 4) (group 2) (diagonal 3) (id 10) (value any)))
(assert (possible (row 1) (column 5) (group 2) (diagonal 3) (id 11) (value any)))
(assert (possible (row 1) (column 6) (group 2) (diagonal 2) (id 12) (value 2)))
(assert (possible (row 2) (column 4) (group 2) (diagonal 3) (id 13) (value any)))
(assert (possible (row 2) (column 5) (group 2) (diagonal 2) (id 14) (value any)))
(assert (possible (row 2) (column 6) (group 2) (diagonal 3) (id 15) (value 4)))
(assert (possible (row 3) (column 1) (group 3) (diagonal 3) (id 19) (value any)))
(assert (possible (row 3) (column 2) (group 3) (diagonal 3) (id 20) (value any)))
(assert (possible (row 3) (column 3) (group 3) (diagonal 1) (id 21) (value any)))
(assert (possible (row 4) (column 1) (group 3) (diagonal 3) (id 22) (value any)))
(assert (possible (row 4) (column 2) (group 3) (diagonal 3) (id 23) (value 5)))
(assert (possible (row 4) (column 3) (group 3) (diagonal 2) (id 24) (value 6)))
(assert (possible (row 3) (column 4) (group 4) (diagonal 2) (id 28) (value 5)))
(assert (possible (row 3) (column 5) (group 4) (diagonal 3) (id 29) (value any)))
(assert (possible (row 3) (column 6) (group 4) (diagonal 3) (id 30) (value any)))
(assert (possible (row 4) (column 4) (group 4) (diagonal 1) (id 31) (value any)))
(assert (possible (row 4) (column 5) (group 4) (diagonal 3) (id 32) (value 3)))
(assert (possible (row 4) (column 6) (group 4) (diagonal 3) (id 33) (value any)))
(assert (possible (row 5) (column 1) (group 5) (diagonal 3) (id 37) (value 5)))
(assert (possible (row 5) (column 2) (group 5) (diagonal 2) (id 38) (value any)))
(assert (possible (row 5) (column 3) (group 5) (diagonal 3) (id 39) (value any)))
(assert (possible (row 6) (column 1) (group 5) (diagonal 2) (id 40) (value any)))
(assert (possible (row 6) (column 2) (group 5) (diagonal 3) (id 41) (value any)))
(assert (possible (row 6) (column 3) (group 5) (diagonal 3) (id 42) (value any)))
(assert (possible (row 5) (column 4) (group 6) (diagonal 3) (id 46) (value any)))
(assert (possible (row 5) (column 5) (group 6) (diagonal 1) (id 47) (value any)))
(assert (possible (row 5) (column 6) (group 6) (diagonal 3) (id 48) (value any)))
(assert (possible (row 6) (column 4) (group 6) (diagonal 3) (id 49) (value 1)))
(assert (possible (row 6) (column 5) (group 6) (diagonal 3) (id 50) (value any)))
(assert (possible (row 6) (column 6) (group 6) (diagonal 1) (id 51) (value any)))

)
