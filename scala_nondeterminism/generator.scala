trait Generator[+A] {
  def next(): Option[A]
  def reset(): Unit

  def and[B](f: A => Generator[B]): Generator[B] = {
    val self = this

    new Generator[B] {
      // ---BEGIN CONSTRUCTOR---
      private var curGenerator: Option[Generator[B]] = None
      updateGenerator()
      // ---END CONSTRUCTOR---

      private def updateGenerator() {
        curGenerator = self.next().map(f)
      }

      @scala.annotation.tailrec
      def next(): Option[B] = {
        curGenerator match {
          case Some(gen) => {
            gen.next() match {
              case s@Some(_) => s
              case None => {
                updateGenerator()
                next()
              }
            }
          }
          case None => None
        }
      }

      def reset() {
        self.reset()
      }
    }
  }

  def or[B >: A](otherRaw: => Generator[B]): Generator[B] = {
    lazy val other = otherRaw
    val self = this
    new Generator[B] {
      def next(): Option[B] = {
        self.next() match {
          case s@Some(_) => s
          case None => other.next()
        }
      }
      def reset() {
        self.reset()
        other.reset()
      }
    }
  }

  def flatMap[B](f: A => Generator[B]): Generator[B] = and(f)
  def map[B](f: A => B): Generator[B] = {
    val self = this
    new Generator[B] {
      def next(): Option[B] = {
        self.next().map(f)
      }
      def reset() {
        self.reset()
      }
    }
  }
}

class EmptyGenerator[A] extends Generator[A] {
  def next(): Option[A] = None
  def reset() {}
}

class SingletonGenerator[A](val value: A) extends Generator[A] {
  private var gotten = false
  def next(): Option[A] = {
    if (gotten) {
      None
    } else {
      gotten = true
      Some(value)
    }
  }

  def reset() {
    gotten = false
  }
}

object Generator {
  import scala.language.implicitConversions

  implicit def anyToGenerator[A](a: A): Generator[A] = {
    new SingletonGenerator(a)
  }
}
