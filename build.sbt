name := """play-test-project"""
// organization := "com.example"

version := "1.0-SNAPSHOT"

lazy val root = (project in file(".")).enablePlugins(PlayScala)

scalaVersion := "2.12.4"

libraryDependencies += "org.scalatestplus.play" %% "scalatestplus-play" % "3.1.2" % Test
libraryDependencies += guice

libraryDependencies ++= Seq(
  jdbc,
  cache,
  // "org.postgresql" % "postgresql" % "9.4-1201-jdbc41",
  ws
)