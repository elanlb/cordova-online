name := """super-inbox"""
// organization := "com.example"

version := "1.0-SNAPSHOT"

lazy val root = (project in file(".")).enablePlugins(PlayScala)

scalaVersion := "2.12.4"

libraryDependencies += "javax.xml.bind" % "jaxb-api" % "2.3.0"

libraryDependencies += guice
libraryDependencies += "org.scalatestplus.play" %% "scalatestplus-play" % "3.1.2" % Test

libraryDependencies ++= Seq(
  jdbc,
  "org.postgresql" % "postgresql" % "9.4-1201-jdbc41",
  "com.google.api-client" % "google-api-client" % "1.23.0"
)