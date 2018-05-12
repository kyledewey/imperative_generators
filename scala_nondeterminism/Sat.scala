sealed trait BooleanExpression
case class Literal(variable: String, isPositive: Boolean) extends BooleanExpression
case class And(b1: BooleanExpression, b2: BooleanExpression) extends BooleanExpression
case class Or(b1: BooleanExpression, b2: BooleanExpression) extends BooleanExpression

object Sat {
  type Env = Map[String, Boolean]
  import Generator._

  def solutions(exp: BooleanExpression, input: Env): Generator[Env] = {
    exp match {
      case Literal(x, isPositive) => {
        input.get(x) match {
          case Some(`isPositive`) => input: Generator[Env]
          case Some(_) => new EmptyGenerator
          case None => (input + (x -> isPositive)): Generator[Env]
        }
      }
      case And(b1, b2) => {
        for {
          tempEnv <- solutions(b1, input)
          result <- solutions(b2, tempEnv)
        } yield result
      }
      case Or(b1, b2) => {
        solutions(b1, input).or(
          solutions(b2, input))
      }
    }
  }

  def solutions(exp: BooleanExpression): Generator[Env] = {
    solutions(exp, Map())
  }
}
