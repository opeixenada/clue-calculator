name := "clue-challenge"

version := "0.1"

scalaVersion := "2.13.1"

val akkaHttpVersion = "10.1.11"
val akkaVersion = "2.6.1"

libraryDependencies ++= Seq(
  "com.typesafe.akka" %% "akka-stream" % akkaVersion,
  "com.typesafe.akka" %% "akka-http" % akkaHttpVersion,
  "com.typesafe.akka" %% "akka-http-spray-json" % akkaHttpVersion
)