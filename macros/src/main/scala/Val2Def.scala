import scala.annotation.{StaticAnnotation, compileTimeOnly}
import scala.language.experimental.macros
import scala.reflect.macros.whitebox

@compileTimeOnly("enable macro paradise to expand macro annotations")
class Val2Def extends StaticAnnotation {
  def macroTransform(annottees: Any*): Any = macro Val2Def.impl
}

object Val2Def {

  def impl(c: whitebox.Context)(annottees: c.Expr[Any]*): c.Expr[Any] = {
    import c.universe._

    annottees.map(_.tree) match {
      case (defDeclaration: DefDef) :: Nil =>
        c.warning(c.enclosingPosition, "@@@ def" + defDeclaration.toString())
      case (valDeclaration: ValDef) :: Nil =>
        c.warning(c.enclosingPosition, "@@@ val" + valDeclaration.toString())
      case _ => c.abort(c.enclosingPosition, "Invalid annottee")
    }

    val str = " from macros"

    c.Expr[Unit](q"def test(int: Int): String = int.toString + $str")
  }
}
