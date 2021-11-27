object Main {

  def main(args: Array[String]): Unit = {

//    SimpleMacro.hello
//    SimpleMacro.hello2
//
//    Asserts.assert(2 < 3, "Assert failed")
//    Asserts.assertEqual(3, 4)

    println(Show.show(X(66, "test")))

    println("End!")
  }

  case class X(int: Int, string: String)
}
