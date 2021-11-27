import scala.language.experimental.macros
import scala.reflect.macros.blackbox

object Asserts {

  val assertionEnabled = true

  def assert(cond: Boolean, msg: Any): Unit = macro assertImpl

  def assertEqual(act: Any, exp: Any): Unit = macro assertEqualImpl

  def assertImpl(
      c: blackbox.Context
  )(cond: c.Expr[Boolean], msg: c.Expr[Any]): c.Expr[Unit] = {
    import c.universe._

    if (assertionEnabled) {
      reify[Unit] {
        if (cond.splice) {
          ()
        } else {
          throw new AssertionError(s"Condition is false: ${msg.splice} ")
        }
      }
    } else {
      reify[Unit] { println("assertion is true") }
    }
  }
  def assertEqualImpl(
      c: blackbox.Context
  )(act: c.Expr[Any], exp: c.Expr[Any]): c.Expr[Unit] = {
    import c.universe._
    val actm = act.tree.toString
    val expm = exp.tree.toString
    reify({
      if (act.splice != exp.splice) {
        try {
          throw new Exception(
            "AssertionError: " + c
              .Expr[String](Literal(Constant(actm)))
              .splice + "[" + act.splice + "]==[" + exp.splice + "]" + c
              .Expr[String](Literal(Constant(expm)))
              .splice
          )
        } catch {
          case unknown: Throwable =>
            System.err.println(
              "" + unknown + unknown.getStackTrace.toList
                .filter(_.toString.indexOf("scala.") != 0)
                .mkString("\n  ", "\n  ", "\n  ")
            );
        }
      }
    })
  }

}
