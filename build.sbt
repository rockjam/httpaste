name := """happypaste"""

version := "1.0-SNAPSHOT"

organization := "com.github.rockjam"

scalaVersion := "2.12.2"

libraryDependencies ++= Seq(
  "org.scalatest" %% "scalatest" % "3.0.3" % Test
)

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
)

wartremoverErrors in (Compile, compile) ++= Warts.unsafe