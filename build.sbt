version in ThisBuild := "0.0.1-SNAPSHOT"

lazy val root = project
  .in(file("."))
  .settings(name := "happypaste")
  .settings(commonSettings)
  .settings(noPublish)
  .settings(
    libraryDependencies += "org.scalaj" %% "scalaj-http" % "2.3.0"
  )
  .dependsOn(interpolators)
  .aggregate(interpolators)


lazy val interpolators = project
  .settings(commonSettings)
  .settings(libraryDependencies ++= Seq(
    "org.scalaj" %% "scalaj-http" % "2.3.0",
    "com.lihaoyi" %% "fastparse" % "0.4.3",
    "org.scala-lang" % "scala-reflect" % "2.12.2",
    "org.scalatest" %% "scalatest" % "3.0.3" % Test
  ))

lazy val commonSettings = Seq(
  organization := "com.github.rockjam",
  scalaVersion := "2.12.2",
  scalacOptions ++= Seq(
    "-deprecation",
    "-encoding", "UTF-8",
    "-feature",
    "-language:existentials",
    "-language:higherKinds",
    "-language:implicitConversions",
    "-unchecked",
    "-Xfatal-warnings",
    "-Xlint",
    "-Yno-adapted-args",
    "-Ywarn-dead-code",
    "-Ywarn-numeric-widen",
    "-Ywarn-value-discard",
    "-Xfuture"
  ),
  (scalacOptions in(Compile, console)) := scalacOptions.value.filterNot(_ == "-Xfatal-warnings")
)

lazy val noPublish = Seq(
  publish := {},
  publishLocal := {},
  publishArtifact := false
)
