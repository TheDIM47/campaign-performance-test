name := "campaign-performance-test"

version := "1.0"

scalaVersion := "2.11.7"

scalacOptions in ThisBuild := Seq("-feature", "-unchecked", "-deprecation", "-encoding", "utf8", "-Xlint")

val sprayVersion = "1.3.1"

libraryDependencies ++= Seq(
  "org.scala-lang.modules" %% "scala-async" % "0.9.2"
  , "com.typesafe.akka" %% "akka-actor" % "2.3.6"
  , "com.typesafe.akka" %% "akka-http-experimental" % "0.7"
  , "io.spray" %% "spray-client" % sprayVersion
  , "io.spray" %% "spray-routing" % sprayVersion
  , "io.spray" %% "spray-testkit" % sprayVersion % "test"
  , "org.mongodb" %% "casbah" % "2.8.2"
  , "org.json4s" %% "json4s-jackson" % "3.2.11"
  , "com.typesafe.scala-logging" %% "scala-logging-slf4j" % "2.1.2"
  , "ch.qos.logback" % "logback-classic" % "1.1.2"
  , "org.scalatest" %% "scalatest" % "2.2.4" % "test"
)
