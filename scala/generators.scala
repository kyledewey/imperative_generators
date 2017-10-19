trait Generator[+A] {
  def makeIterator(depth: Int): Iterator[A]

  def ~[B](otherRaw: => Generator[B]): Generator[~[A, B]] = {
    lazy val other = otherRaw
    val self = this
    new Generator[~[A, B]] {
      def makeIterator(depth: Int): Iterator[~[A, B]] = {
        Iterator.and(self.makeIterator(depth),
                     other.makeIterator(depth),
                     (a: A, b: B) => new ~(a, b))
      }
    }
  }

  def |[B >: A](otherRaw: => Generator[B]): Generator[B] = {
    lazy val other = otherRaw
    val self = this
    new Generator[B] {
      def makeIterator(depth: Int): Iterator[B] = {
        Iterator.or(self.makeIterator(depth),
                    other.makeIterator(depth))
      }
    }
  }

  def ^^[B](f: A => B): Generator[B] = {
    val self = this
    new Generator[B] {
      def makeIterator(depth: Int): Iterator[B] = {
        if (depth > 0) {
          Iterator.map(self.makeIterator(depth - 1), f)
        } else {
          EmptyIterator
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
      def makeIterator(depth: Int): Iterator[A] = {
        Iterator.singletonIterator(a)
      }
    }
  }
}

case class ~[+A, +B](_1: A, _2: B)

