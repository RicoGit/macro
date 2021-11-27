import scala.language.experimental.macros
import scala.reflect.macros.blackbox

trait Show[A] {
  def show(a: A): String
}
object Show {
  def show[A](a: A)(implicit show: Show[A]): String = show.show(a)

  implicit def show[A]: Show[A] = macro showImpl[A]

  def showImpl[A](
      c: blackbox.Context
  )(a: c.WeakTypeTag[A]): c.Expr[Show[A]] = {
    import c.universe._
    val tree = q"""
        new Show[$a] {
          def show(a: $a): String = a.toString
        }
       """
    c.Expr[Show[A]](tree)
  }
}
