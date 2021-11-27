import scala.language.experimental.macros
import scala.reflect.macros.blackbox.Context

object SimpleMacro {

  def hello: Unit = macro impl

  def impl(c: Context): c.Expr[Unit] = {
    import c.universe._
    c.Expr[Unit](q"""println("Hello World 1")""")
  }

  def hello2: Unit = macro impl2

  def impl2(c: Context): c.Expr[Unit] = {
    import c.universe._
    reify[Unit](
      println("Hello World 2")
    )
  }

}
