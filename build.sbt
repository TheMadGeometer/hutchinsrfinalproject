name := "hutchinsrfinalproject"

version := "0.1"

scalaVersion := "2.12.8"

val http4sVersion = "0.18.21"

scalacOptions ++= Seq(
  "-target:jvm-1.8",
  "-encoding", "UTF-8",
  "-unchecked",
  "-deprecation",
  "-feature",
  "-Xfuture",
  "-Yno-adapted-args",
  "-Ypartial-unification",
  "-Ywarn-dead-code",
  "-Ywarn-numeric-widen",
  "-Ywarn-value-discard",
  "-Ywarn-unused"
)

libraryDependencies ++= Seq(
  "com.typesafe.akka" %% "akka-http"   % "10.1.7",
  "com.typesafe.akka" %% "akka-stream" % "2.5.19",
  "org.http4s" %% "http4s-dsl" % http4sVersion,
  "org.http4s" %% "http4s-server" % http4sVersion,
  "org.http4s" %% "http4s-blaze-server" % http4sVersion,
  "org.http4s" %% "http4s-blaze-client" % http4sVersion,
  "org.apache.commons" % "commons-dbcp2" % "2.4.0",
  "org.http4s" %% "http4s-circe" % http4sVersion,
  // Optional for auto-derivation of JSON codecs
  "io.circe" %% "circe-generic" % "0.9.3",
  // Optional for string interpolation to JSON model
  "io.circe" %% "circe-literal" % "0.9.3",
  "io.circe" %% "circe-parser" % "0.9.3",
  "mysql" % "mysql-connector-java" % "5.1.44"
)
