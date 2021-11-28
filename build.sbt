name := "macro"

version := "0.2"

val defaultScalacOpt = List(
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
  "-Xlog-free-terms"
//  "-Ymacro-debug-lite"
)

inThisBuild(
  scalaVersion := "2.13.7"
)

lazy val root =
  project
    .in(file("."))
    .settings(scalacOptions ++= defaultScalacOpt)
    .aggregate(macros, main)

lazy val macros = project
  .in(file("macros"))
  .settings(
    scalacOptions ++= defaultScalacOpt,
    libraryDependencies ++= Seq(
      "org.scala-lang" % "scala-reflect" % scalaVersion.value,
      "dev.zio" %% "zio" % "1.0.12"
    )
  )
lazy val main = project
  .in(file("main"))
  .settings(scalacOptions ++= defaultScalacOpt)
  .dependsOn(macros)
