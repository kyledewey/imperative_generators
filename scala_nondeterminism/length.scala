object Length {
  import LP._
  import Scope._

  def baseCase(list: Term, len: Term): LP = {
    implicit val scope = new Scope
    for {
      _ <- unify(list, nil)
      _ <- unify(len, 0)
    } yield ()
  }

  def recursiveCase(list: Term, len: Term): LP = {
    implicit val scope = new Scope
    for {
      _ <- unify(list, cons('h, 't))
      _ <- length('t, 'tailLength)
      _ <- is(len, Seq('tailLength), { case Seq(i) => i + 1 })
    } yield ()
  }

  def length(t: Term, len: Term): LP = {
    implicit val scope = new Scope
    baseCase(t, len).or(
      recursiveCase(t, len))
  }

  def testEmptyList() {
    implicit val scope = new Scope
    val query = length(nil, 'len)
    LP.runner(query, Seq('len))
  }

  def testNonEmptyList() {
    implicit val scope = new Scope
    val query = length(mkList("a", "b", "c"), 'len)
    LP.runner(query, Seq('len))
  }
}
