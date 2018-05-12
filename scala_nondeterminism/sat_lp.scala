object SatLP {
  import LP._
  import Scope._

  def solveLiteral(t: Term): LP = {
    implicit val s = new Scope
    for {
      _ <- unify(t, 'literal('x, 'x))
    } yield ()
  }

  def solveAnd(t: Term): LP = {
    implicit val s = new Scope
    for {
      _ <- unify(t, 'and('b1, 'b2))
      _ <- solve('b1)
      _ <- solve('b2)
    } yield ()
  }

  def solveOr(t: Term): LP = {
    implicit val s = new Scope
    unify(t, 'or('b1, 'b2)).and(_ =>
      solve('b1).or(
        solve('b2)))
  }

  def solve(t: Term): LP = {
    solveLiteral(t).or(
      solveAnd(t).or(
        solveOr(t)))
  }

  def testNoSolutions() {
    implicit val s = new Scope
    val query = solve('and('literal('x, "true"),
                           'literal('x, "false")))
    LP.runner(query, Seq('x))
  }

  def testTwoSolutions() {
    implicit val s = new Scope
    val query = solve('or('literal('x, "true"),
                          'literal('x, "false")))
    LP.runner(query, Seq('x))
  }
}
