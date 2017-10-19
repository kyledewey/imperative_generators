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

object Main {
  def main(args: Array[String]) {
    if (args.length != 1) {
      println("Needs an integer depth bound")
    } else {
      new GenAsScalaIterator(Exp.exp.makeGenIterator(args(0).toInt)).foreach(a => println(a.asString))
    }
  } // main
}
