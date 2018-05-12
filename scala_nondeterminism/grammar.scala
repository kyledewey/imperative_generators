sealed trait Exp {
  def asString(): String
}
case class IntLiteral(i: Int) extends Exp {
  def asString(): String = i.toString
}
case class Binop(e1: Exp, op: Op, e2: Exp) extends Exp {
  def asString(): String = {
    Seq(e1.asString, op.asString, e2.asString).mkString(" ")
  }
}
case class Paren(e: Exp) extends Exp {
  def asString(): String = {
    "(" + e.asString + ")"
  }
}

sealed trait Op {
  def asString(): String
}
case object Plus extends Op {
  def asString(): String = "+"
}
case object Minus extends Op {
  def asString(): String = "-"
}
case object Mult extends Op {
  def asString(): String = "*"
}
case object Div extends Op {
  def asString(): String = "/"
}

object Exp {
  import Generator._

  def literal(): Generator[IntLiteral] = {
    IntLiteral(0).or(
      IntLiteral(1).or(
        IntLiteral(2)))
  }

  def op(): Generator[Op] = {
    (Plus: Op).or(
      (Minus: Op).or(
        (Mult: Op).or(
          Div: Op)))
  }

  def withDecBound[A](depth: Int)(f: Int => Generator[A]): Generator[A] = {
    if (depth > 0) {
      f(depth - 1)
    } else {
      EmptyGenerator
    }
  }

  def binop(depth: Int): Generator[Binop] = {
    withDecBound(depth)(depth =>
      for {
        e1 <- exp(depth)
        o <- op
        e2 <- exp(depth)
      } yield Binop(e1, o, e2))
  }

  def paren(depth: Int): Generator[Paren] = {
    withDecBound(depth)(depth =>
      for {
        e <- exp(depth)
      } yield Paren(e))
  }

  def exp(depth: Int): Generator[Exp] = {
    (literal: Generator[Exp]).or(
      (binop(depth): Generator[Exp]).or(
        (paren(depth): Generator[Exp])))
  }
}

