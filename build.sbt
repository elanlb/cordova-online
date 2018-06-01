name := """gphotos-tagger"""
// organization := "com.example"

version := "1.0-SNAPSHOT"

lazy val root = (project in file(".")).enablePlugins(PlayScala)

scalaVersion := "2.12.4"

libraryDependencies ++= Seq(
  "org.scalatestplus.play" %% "scalatestplus-play" % "3.1.2" % Test,
  jdbc,
  "org.postgresql" % "postgresql" % "9.4-1201-jdbc41",
  guice,
  "com.google.api-client" % "google-api-client" % "1.23.0"
)