object Append {
  import LP._
  import Scope._

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
}
