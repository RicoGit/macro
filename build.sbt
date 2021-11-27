name := "macro"

version := "0.1"

scalaVersion := "2.13.7"

scalacOptions ++= List(
  "-encoding",
  "utf-8",
  "-g:vars",
  "-Ymacro-annotations",
  "-feature",
  "-language:higherKinds",
  "-language:existentials",
  "-language:implicitConversions",
  "-language:experimental.macros",
  "-language:postfixOps",
  "-explaintypes",
  "-Ywarn-macros:after",
  "-Xlog-free-terms",
  "-Ymacro-debug-lite"
)

lazy val root =
  project
    .in(file("."))
    .aggregate(macros, main)

lazy val macros = project
  .in(file("macros"))
  .settings(
    libraryDependencies ++= Seq(
      "org.scala-lang" % "scala-reflect" % scalaVersion.value
    )
  )
lazy val main = project
  .in(file("main"))
  .dependsOn(macros)
