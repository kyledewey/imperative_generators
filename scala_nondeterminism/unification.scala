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
      mapping(x)
    } else {
      val retval = Placeholder()
      mapping += (x -> retval)
      retval
    }
  }
}

class PreStructure(val name: String) {
  import Scope._

  def apply[A](a: A)(implicit scope: Scope,
                     aEv: Termable[A]): Term = {
    Structure(name, Seq(aEv.asTerm(a)(scope)))
  }

  def apply[A, B](a: A, b: B)(implicit scope: Scope,
                              aEv: Termable[A],
                              bEv: Termable[B]): Term = {
    Structure(name, Seq(aEv.asTerm(a)(scope),
                        bEv.asTerm(b)(scope)))
  }

  def apply[A, B, C](a: A, b: B, c: C)(implicit scope: Scope,
                                       aEv: Termable[A],
                                       bEv: Termable[B],
                                       cEv: Termable[C]): Term = {
    Structure(name, Seq(aEv.asTerm(a)(scope),
                        bEv.asTerm(b)(scope),
                        cEv.asTerm(c)(scope)))
  }
}

object Scope {
  import scala.language.implicitConversions

  trait Termable[A] {
    def asTerm(a: A)(implicit scope: Scope): Term
  }

  implicit object SymbolIsTermable extends Termable[Symbol] {
    def asTerm(s: Symbol)(implicit scope: Scope): Term = scope.asVar(s)
  }

  implicit object IntIsTermable extends Termable[Int] {
    def asTerm(i: Int)(implicit scope: Scope): Term = IntTerm(i)
  }

  // Strings act as atoms
  implicit object StringIsTermable extends Termable[String] {
    def asTerm(s: String)(implicit scope: Scope): Term = Structure(s, Seq())
  }

  implicit object TermIsTermable extends Termable[Term] {
    def asTerm(t: Term)(implicit scope: Scope): Term = t
  }

  implicit def termableToTerm[A](a: A)(implicit ev: Termable[A], scope: Scope): Term = {
    ev.asTerm(a)(scope)
  }

  implicit def symbolToPreStructure(s: Symbol): PreStructure = {
    new PreStructure(s.name)
  }

  val nil: Term = Structure("[]", Seq())

  def cons(head: Term, tail: Term): Term = {
    Structure(".", Seq(head, tail))
  }
                                   
  def mkList(terms: Term*): Term = {
    terms.foldRight(nil)(cons)
  }
}
