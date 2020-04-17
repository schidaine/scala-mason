import Dependencies._

ThisBuild / scalaVersion     := "2.13.1"
ThisBuild / version          := "1.0.0"
ThisBuild / organization     := "schidaine"
ThisBuild / organizationName := "Sylvain Chidaine"

val playJson  = "com.typesafe.play" %% "play-json" % "2.8.1"

lazy val root = (project in file("."))
  .settings(
    name := "mason-lib",
    libraryDependencies += playJson,
    libraryDependencies += scalaTest % Test
  )

// See https://www.scala-sbt.org/1.x/docs/Using-Sonatype.html for instructions on how to publish to Sonatype.
