object Append {
  import LP._
  import Scope._

  // append([], List, List)
  // append([H|T], List, [H|Rest]) :-
  //     append(T, List, Rest).
  //
  def append(l1: Term, l2: Term, l3: Term): LP = {
    implicit val scope = new Scope
    val baseCase: LP = {
      for {
        _ <- unify(l1, nil)
        _ <- unify(l2, l3)
      } yield ()
    }
    lazy val recursiveCase: LP = {
      for {
        _ <- unify(l1, cons('h, 't))
        _ <- unify(l3, cons('h, 'rest))
        _ <- append('t, l2, 'rest)
       } yield ()
    }
    baseCase.or(recursiveCase)
  }

  def appendExplicit(l1: Term, l2: Term, l3: Term): LP = {
    val baseCase: LP = {
      for {
        _ <- unify(l1, nil)
        _ <- unify(l2, l3)
      } yield ()
    }
    val h = Placeholder()
    val t = Placeholder()
    val rest = Placeholder()
    lazy val recursiveCase: LP = {
      for {
        _ <- unify(l1, cons(h, t))
        _ <- unify(l3, cons(h, rest))
        _ <- appendExplicit(t, l2, rest)
       } yield ()
    }
    baseCase.or(recursiveCase)
  }
  
  // append([1, 2], [3, 4], List)
  def testInInOut() {
    implicit val scope = new Scope
    val query = append(mkList(1, 2), mkList(3, 4), 'list)
    LP.runner(query, Seq('list))
  }

  // append(L1, L2, [1, 2, 3, 4])
  def testOutOutIn() {
    implicit val scope = new Scope
    val query = append('l1, 'l2, mkList(1, 2, 3, 4))
    LP.runner(query, Seq('l1, 'l2))
  }
}
