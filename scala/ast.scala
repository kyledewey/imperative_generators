sealed trait Exp
case class IntLiteral(i: Int) extends Exp
case class Binop(e1: Exp, op: Op, e2: Exp) extends Exp
case class Paren(e: Exp) extends Exp

sealed trait Op
case object Plus extends Op
case object Minus extends Op
case object Mult extends Op
case object Div extends Op

import Generator._

object Exp {
  lazy val literal: Generator[IntLiteral] =
    IntLiteral(0) | IntLiteral(1) | IntLiteral(2)

  lazy val op: Generator[Op] =
    Plus | Minus | Mult | Div

  lazy val binop: Generator[Binop] =
    exp ~ op ~ exp ^^ { case e1 ~ o ~ e2 => Binop(e1, o, e2) }

  lazy val paren: Generator[Paren] =
    exp ^^ Paren.apply

  lazy val exp: Generator[Exp] =
    literal | binop | paren
}
