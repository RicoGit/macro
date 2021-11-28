import zio.{Has, Managed, ZIO, ZLayer}
import zio.clock.Clock
import zio.random.Random

object Main {

  def main(args: Array[String]): Unit = {

//    SimpleMacro.hello
//    SimpleMacro.hello2
//
//    Asserts.assert(2 < 3, "Assert failed")
//    Asserts.assertEqual(3, 4)

//    println(Show.show(X(66, "test")))

//    @Val2Def
//    val func: Int => String = _.toString
//
//    println(test(1))

    @ProvideLayer
    val someLayer: ZLayer[Has[String] with Has[Random.Service] with Has[
      Clock.Service
    ], Nothing, Has[Unit]] = {
      (for {
        clock <- ZIO.service[Clock.Service]
        random <- ZIO.service[Random.Service]
        str <- ZIO.service[String]
      } yield println(s"Hello from macro with $str")).toLayer
    }

    // macro rewrite to method like this:

    def provideUnit(
        clock: Clock.Service,
        random: Random.Service
    ): Managed[Nothing, Has[Unit]] = {
      Has.allOf(1, 2, 4)
      val layer: ZLayer[Random with Clock, Nothing, Has[Unit]] = (for {
        clock <- ZIO.environment[Clock]
        random <- ZIO.environment[Random]
      } yield ()).toLayer

      val value = {
        Has.allOf(clock, random)
      }
      layer.build.provide(value)
    }

    val managed: Managed[Nothing, Has[Unit]] = someLayer("ENV!", null, null)
    zio.Runtime.default.unsafeRunSync(managed.useNow)

    println("End!")
  }

  case class X(int: Int, string: String)
}
