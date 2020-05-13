import Dependencies._

import xerial.sbt.Sonatype._

ThisBuild / scalaVersion     := "2.13.1"
ThisBuild / version          := "1.2.0"
ThisBuild / organization     := "io.github.schidaine"
ThisBuild / organizationName := "Sylvain Chidaine"

lazy val root = (project in file("."))
  .settings(
    organization := "io.github.schidaine",
    name := "scala-mason",
    description := "A scala library for Mason, a HATEOAS JSON format",
    publishMavenStyle := true,
    sonatypeProjectHosting := Some(GitHubHosting(
      "schidaine",
      "scala-mason",
      "25348343+schidaine@users.noreply.github.com")),
    licenses := List("MIT License" -> new URL("http://www.opensource.org/licenses/mit-license.php")),
    publishTo := sonatypePublishToBundle.value,
    libraryDependencies += playJson,
    libraryDependencies += scalaTest % Test
  )
