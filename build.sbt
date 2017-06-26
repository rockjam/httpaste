version in ThisBuild := "0.0.2"

lazy val root = project
  .in(file("."))
  .settings(name := "httpaste")
  .settings(commonSettings)
  .settings(noPublish)
  .dependsOn(
    `httpaste-akkahttp`,
    `httpaste-curl`,
    `httpaste-scalajhttp`
  )
  .aggregate(
    `httpaste-akkahttp`,
    `httpaste-core`,
    `httpaste-curl`,
    `httpaste-scalajhttp`
  )

lazy val `httpaste-curl` = project
    .settings(commonSettings)
    .settings(publishSettings)
    .settings(libraryDependencies ++= Seq(
      "com.lihaoyi" %% "fastparse" % "0.4.3",
      "org.scala-lang" % "scala-reflect" % "2.12.2",
      "org.scalatest" %% "scalatest" % "3.0.3" % Test
    ))
    .dependsOn(`httpaste-core`)

lazy val `httpaste-scalajhttp` = project
    .settings(commonSettings)
    .settings(publishSettings)
    .settings(libraryDependencies ++= Seq(
      "org.scalaj" %% "scalaj-http" % "2.3.0"
    ))
    .dependsOn(`httpaste-core`)

lazy val `httpaste-akkahttp` = project
  .settings(commonSettings)
  .settings(publishSettings)
  .settings(libraryDependencies ++= Seq(
    "com.typesafe.akka" %% "akka-http" % "10.0.8"
  ))
  .dependsOn(`httpaste-core`)

lazy val `httpaste-core` = project
  .settings(commonSettings)
  .settings(publishSettings)

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

lazy val publishSettings = Seq(
  licenses += ("Apache-2.0", url("https://www.apache.org/licenses/LICENSE-2.0")),
  scmInfo := Some(
    ScmInfo(
      url("https://github.com/rockjam/httpaste"),
      "scm:git@github.com:rockjam/httpaste.git"
    )
  ),
  developers := List(
    Developer(
      "rockjam",
      "Nikolay Tatarinov",
      "5min4eq.unity@gmail.com",
      url("https://github.com/rockjam")
    )
  )
)

lazy val noPublish = Seq(
  publish := {},
  publishLocal := {},
  publishArtifact := false
)
