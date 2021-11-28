import scala.annotation.{StaticAnnotation, compileTimeOnly, tailrec}
import scala.reflect.api.Trees
import scala.reflect.macros.whitebox

@compileTimeOnly("enable macro paradise to expand macro annotations")
final class ProvideLayer extends StaticAnnotation {
  def macroTransform(annottees: Any*): Any = macro ProvideLayer.impl
}

object ProvideLayer {

  def impl(c: whitebox.Context)(annottees: c.Expr[Any]*): c.Expr[Any] = {
    import c.universe._

    case class ValDetail(name: TermName, tpt: Tree, expr: Tree)

    def extractRequirements(tree: ValDef): ValDetail = {

      tree match {
        case q"$_ val $name = $_" =>
          c.abort(
            c.enclosingPosition,
            s"please provide type for variable $name explicitly"
          )
        case q"$_ val $name: $tpt = $expr" => ValDetail(name, tpt, expr)
        case _ =>
          c.abort(c.enclosingPosition, "Invalid val expression")
      }
    }

    def getAllHas(tree: Tree): List[Tree] = {
      tree match {
        // zero dep case
        case tq"" => Nil
        // single dep case
        case tq"Has[$tpt]" => List(tpt)
        // many deps case
        case tq"Has[$tpt] with ..$rest" =>
          tpt +: rest.toList.flatMap(has => getAllHas(has))
        case _ => c.abort(c.enclosingPosition, "Invalid Has type")
      }
    }

    def genParamNames(paramTypes: List[Tree]) =
      paramTypes
        .map { _.toString().toLowerCase.split('.').head }
        .map(name => Ident(TermName(name)))

    // todo handle type aliasing Random == Has[Random.Service]
    val result = annottees.map(_.tree) match {
      case (valDeclaration: ValDef) :: Nil =>
        val ValDetail(name, tpt, expr) = extractRequirements(valDeclaration)

        tpt match {
          case tq"ZLayer[$envType, $errType, Has[$valType]]" =>
            val paramsTypes = getAllHas(envType)
            val paramsNames = genParamNames(paramsTypes)
            val params = paramsNames
              .zip(paramsTypes)
              .map { case (name, tpe) => q"$name: $tpe" }

            val providedDeps = q"Has.allOf[..$paramsTypes](..$paramsNames)"

            val res =
              q"def $name(..$params):  Managed[$errType, Has[$valType]] = { $expr.build.provide($providedDeps) }"

            c.info(c.enclosingPosition, s"Macro expands to '${res}'", true)

            res
          case other =>
            c.abort(
              c.enclosingPosition,
              s"Invalid layer type '$other', aliases isn't allowed"
            )
        }
      case (_: DefDef) :: Nil =>
        c.abort(c.enclosingPosition, "Use 'val' instead of 'def;")
      case _ => c.abort(c.enclosingPosition, "Invalid annottee")
    }

    c.Expr[Any](result)
  }

}
