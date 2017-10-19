trait Iterator[+A] {
  def next(): Option[A]
  def reset(): Unit

  // really only for testing
  def toSeq(): Seq[A] = {
    val retval = scala.collection.mutable.Buffer[A]()
    @scala.annotation.tailrec
    def loop(cur: Option[A]) {
      cur match {
        case Some(a) => {
          retval += a
          loop(next())
        }
        case None => ()
      }
    }
    loop(next())
    retval.toSeq
  }
}

object EmptyIterator extends Iterator[Nothing] {
  def next(): Option[Nothing] = None
  def reset() {}
}

object Iterator {
  def singletonIterator[A](a: A): Iterator[A] = {
    new Iterator[A] {
      private var taken = false
      def next(): Option[A] = {
        if (taken) {
          None
        } else {
          taken = true
          Some(a)
        }
      }
      def reset() {
        taken = false
      }
    }
  }

  def and[A, B, C](first: Iterator[A], second: Iterator[B], f: (A, B) => C): Iterator[C] = {
    new Iterator[C] {
      private var init = true
      private var lastA: Option[A] = None
      def next(): Option[C] = {
        lastA match {
          case Some(a) => {
            second.next() match {
              case Some(b) => Some(f(a, b))
              case None => {
                // go to next A
                second.reset()
                lastA = first.next()
                next()
              }
            }
          }
          case None if init => {
            lastA = first.next()
            init = false
            next()
          }
          case None => None
        }
      }
      def reset() {
        lastA = None
        init = true
        first.reset()
        second.reset()
      }
    }
  } // and

  def or[A](first: Iterator[A], second: Iterator[A]): Iterator[A] = {
    sealed trait WhichIterator
    case object OnFirst extends WhichIterator
    case object OnSecond extends WhichIterator
    case object IsEmpty extends WhichIterator

    new Iterator[A] {
      private var state: WhichIterator = OnFirst
      def next(): Option[A] = {
        state match {
          case OnFirst => {
            first.next() match {
              case Some(a) => Some(a)
              case None => {
                state = OnSecond
                next()
              }
            }
          }
          case OnSecond => {
            second.next() match {
              case Some(a) => Some(a)
              case None => {
                state = IsEmpty
                next()
              }
            }
          }
          case IsEmpty => None
        }
      }

      def reset() {
        state = OnFirst
        first.reset()
        second.reset()
      }
    }
  } // or

  def map[A, B](around: Iterator[A], f: A => B): Iterator[B] = {
    new Iterator[B] {
      def next(): Option[B] = around.next().map(f)
      def reset() {
        around.reset()
      }
    }
  } // map
}


