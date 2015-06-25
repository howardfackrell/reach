name := """reach"""

version := "1.0-SNAPSHOT"

scalaVersion := "2.11.5"

libraryDependencies ++= Seq(
  "org.webjars" %% "webjars-play" % "2.3.0-3",
  "org.webjars" % "jquery" % "2.1.3",
  "org.webjars" % "bootstrap" % "3.1.1-2",
  "org.scoverage" %% "scalac-scoverage-plugin" % "1.0.1" % "test"
)

lazy val root = (project in file(".")).addPlugins(PlayScala)
