trait Generator[+A] {
  def makeGenIterator(depth: Int): GenIterator[A]

  def ~[B](otherRaw: => Generator[B]): Generator[~[A, B]] = {
    lazy val other = otherRaw
    val self = this
    new Generator[~[A, B]] {
      def makeGenIterator(depth: Int): GenIterator[~[A, B]] = {
        GenIterator.and(self.makeGenIterator(depth),
                        other.makeGenIterator(depth),
                        (a: A, b: B) => new ~(a, b))
      }
    }
  }

  def |[B >: A](otherRaw: => Generator[B]): Generator[B] = {
    lazy val other = otherRaw
    val self = this
    new Generator[B] {
      def makeGenIterator(depth: Int): GenIterator[B] = {
        GenIterator.or(self.makeGenIterator(depth),
                       other.makeGenIterator(depth))
      }
    }
  }

  def ^^[B](f: A => B): Generator[B] = {
    val self = this
    new Generator[B] {
      def makeGenIterator(depth: Int): GenIterator[B] = {
        if (depth > 0) {
          GenIterator.map(self.makeGenIterator(depth - 1), f)
        } else {
          EmptyGenIterator
        }
      }
    }
  }
}

object Generator {
  import scala.language.implicitConversions

  // makes a generator that just produces that thing
  implicit def anythingToGenerator[A](a: A): Generator[A] = {
    new Generator[A] {
      def makeGenIterator(depth: Int): GenIterator[A] = {
        GenIterator.singletonGenIterator(a)
      }
    }
  }
}

case class ~[+A, +B](_1: A, _2: B)

