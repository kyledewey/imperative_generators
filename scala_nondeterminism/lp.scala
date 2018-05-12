// The things that we generate are unification environments.
// This gets complicated because LP is two monads in one:
// noneterminism (Generator) and State.  State is over unification
// environments.  A weird thing is that it looks like Unit shows
// up a lot in a monadic context
object LP {
  type Env = UnificationEnvironment

  def unify(t1: Term, t2: Term): LP = {
    new LP {
      def reify(env: Env): Generator[Env] = {
        env.unify(t1, t2) match {
          case Some(newEnv) => new SingletonGenerator(newEnv)
          case None => EmptyGenerator
        }
      }
    }
  }

  def fail(): LP = {
    new LP {
      def reify(env: Env): Generator[Env] = {
        EmptyGenerator
      }
    }
  }

  def runner(query: LP, termsOfInterest: Seq[Term]) {
    val gen = query.reify(new UnificationEnvironment(Map()))
    @scala.annotation.tailrec
    def loop(opEnv: Option[Env]) {
      opEnv match {
        case Some(env) => {
          termsOfInterest.foreach(t =>
            println(env.fullLookup(t).asString))
          println("----------")
          loop(gen.next())
        }
        case None => ()
      }
    }

    loop(gen.next())
  }
}
import LP.Env

trait LP {
  def reify(env: Env): Generator[Env]

  def flatMap(f: Unit => LP): LP = {
    val self = this
    new LP {
      def reify(env: Env): Generator[Env] = {
        lazy val other: LP = f()
        self.reify(env).flatMap(other.reify)
      }
    }
  }

  def map(f: Unit => Unit): LP = {
    new LP {
      def reify(env: Env): Generator[Env] = {
        new SingletonGenerator(env)
      }
    }
  }

  def or(other: => LP): LP = {
    val self = this
    new LP {
      def reify(env: Env): Generator[Env] = {
        self.reify(env).or(other.reify(env))
      }
    }
  }
}
