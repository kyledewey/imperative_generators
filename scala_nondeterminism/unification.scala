sealed trait Term
case class Placeholder(i: Int) extends Term
case class IntTerm(i: Int) extends Term
case class Structure(name: String, terms: Seq[Term]) extends Term

object Placeholder {
  private var nextId = 0

  def apply(): Placeholder = {
    val curId = nextId
    nextId += 1
    Placeholder(curId)
  }
}

class UnificationEnvironment(val mapping: Map[Placeholder, Term]) {
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

class TermContext(private var mapping: Map[Symbol, Placeholder]) {
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
 
object Scope {
  def asVar(s: Symbol)(implicit ev: TermContext): Placeholder = {
    ev.asVar(s)
  }

  val nil: Term = Structure("[]", Seq())

  def cons(head: Term, tail: Term): Term = {
    Structure(".", Seq(head, tail))
  }

  def mkList(terms: Term*): Term = {
    terms.foldRight(nil)(cons)
  }
}