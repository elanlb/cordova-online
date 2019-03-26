name := """Cordova Online"""
// organization := "com.example"

version := "1.0-SNAPSHOT"

lazy val root = (project in file(".")).enablePlugins(PlayScala)

scalaVersion := "2.12.4"


libraryDependencies += guice
// https://mvnrepository.com/artifact/org.scalatestplus.play/scalatestplus-play_2.11
libraryDependencies += "org.scalatestplus.play" %% "scalatestplus-play" % "4.0.0-M2" % Test

libraryDependencies ++= Seq(
  jdbc,
  "org.postgresql" % "postgresql" % "9.4-1201-jdbc41",
  "com.google.api-client" % "google-api-client" % "1.23.0"
)