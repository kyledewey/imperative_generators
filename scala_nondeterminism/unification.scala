sealed trait Term {
  def asString(): String
}
case class Placeholder(i: Int) extends Term {
  def asString(): String = "_" + i
}
case class IntTerm(i: Int) extends Term {
  def asString(): String = i.toString
}
case class Structure(name: String, terms: Seq[Term]) extends Term {
  def asString(): String = {
    if (terms.isEmpty) {
      name
    } else {
      name + "(" + terms.map(_.asString).mkString(", ") + ")"
    }
  }
}

object Placeholder {
  private var nextId = 0

  def apply(): Placeholder = {
    val curId = nextId
    nextId += 1
    Placeholder(curId)
  }
}

class UnificationEnvironment(val mapping: Map[Placeholder, Term]) {
  def fullLookup(t: Term): Term = {
    lookup(t) match {
      case p: Placeholder => p
      case i: IntTerm => i
      case Structure(name, terms) => {
        Structure(name, terms.map(fullLookup))
      }
    }
  }

  def lookup(t: Term): Term = {
    t match {
      case p: Placeholder if mapping.contains(p) =>
        lookup(mapping(p))
      case _ => t
    }
  }

  def unify(t1: Term, t2: Term): Option[UnificationEnvironment] = {
    (lookup(t1), lookup(t2)) match {
      case (t1, t2) if t1 == t2 => Some(this)
      case (p: Placeholder, t) => Some(new UnificationEnvironment(mapping + (p -> t)))
      case (t, p: Placeholder) => Some(new UnificationEnvironment(mapping + (p -> t)))
      case (Structure(name1, terms1), Structure(name2, terms2))
        if name1 == name2 && terms1.size == terms2.size => {
          unify(terms1.zip(terms2))
        }
      case _ => None
    }
  }

  def unify(terms: Seq[(Term, Term)]): Option[UnificationEnvironment] = {
    terms.foldLeft(Some(this): Option[UnificationEnvironment])((res, cur) => {
      res.flatMap(_.unify(cur._1, cur._2))
    })
  }
}

class Scope(private var mapping: Map[Symbol, Placeholder]) {
  def this() = this(Map())

  def asVar(x: Symbol): Placeholder = {
    if (mapping.contains(x)) {
      println("IN ENV: " + x)
      mapping(x)
    } else {
      println("NOT IN ENV: " + x)
      val retval = Placeholder()
      mapping += (x -> retval)
      retval
    }
  }
}
 
object Scope {
  import scala.language.implicitConversions

  implicit def symbolToPlaceholder(s: Symbol)(implicit ev: Scope): Placeholder = {
    ev.asVar(s)
  }

  implicit def intToTerm(i: Int): IntTerm = {
    IntTerm(i)
  }

  val nil: Term = Structure("[]", Seq())

  def cons(head: Term, tail: Term): Term = {
    Structure(".", Seq(head, tail))
  }

  def mkList(terms: Term*): Term = {
    terms.foldRight(nil)(cons)
  }
}
