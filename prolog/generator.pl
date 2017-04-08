int(int(0)).
int(int(1)).
int(int(2)).

op('+').
op('-').
op('*').
op('/').

decBound(In, Out) :-
    In > 0,
    Out is In - 1.

exp(_, Int) :-
    int(Int).
exp(Bound, binop(E1, Op, E2)) :-
    decBound(Bound, NewBound),
    exp(NewBound, E1),
    op(Op),
    exp(NewBound, E2).
exp(Bound, paren(E)) :-
    decBound(Bound, NewBound),
    exp(NewBound, E).

printExp(int(Int)) :-
    write(Int).
printExp(binop(E1, Op, E2)) :-
    printExp(E1),
    write(Op),
    printExp(E2).
printExp(paren(E)) :-
    write('('),
    printExp(E),
    write(')').

main(Bound) :-
    exp(Bound, E),
    printExp(E),
    nl,
    fail.
main(_).
