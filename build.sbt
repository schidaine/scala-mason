import Dependencies._

import xerial.sbt.Sonatype._

lazy val scala212 = "2.12.11"
lazy val scala213 = "2.13.3"
lazy val supportedScalaVersions = List(scala212, scala213)

ThisBuild / scalaVersion     := scala213
ThisBuild / version          := "1.2.2"
ThisBuild / organization     := "io.github.schidaine"
ThisBuild / organizationName := "Sylvain Chidaine"

lazy val root = (project in file("."))
  .settings(
    organization := "io.github.schidaine",
    name := "scala-mason",
    description := "A scala library for Mason, a HATEOAS JSON format",
    crossScalaVersions := supportedScalaVersions,

    // documentation
    siteSubdirName in SiteScaladoc := "api/latest",
    previewPath := "api/latest",
    git.remoteRepo := "git@github.com:schidaine/scala-mason.git",

    // publication
    publishMavenStyle := true,
    sonatypeProjectHosting := Some(GitHubHosting(
      "schidaine",
      "scala-mason",
      "25348343+schidaine@users.noreply.github.com")),
    licenses := List("MIT License" -> new URL("http://www.opensource.org/licenses/mit-license.php")),
    publishTo := sonatypePublishToBundle.value,

    // Dependencies
    libraryDependencies += playJson,
    libraryDependencies += scalaTest % Test
  )
  .enablePlugins(SiteScaladocPlugin)
  .enablePlugins(GhpagesPlugin)
