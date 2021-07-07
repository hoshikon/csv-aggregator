name := "csv-aggregator"

version := "0.1"

scalaVersion := "2.13.6"

idePackagePrefix := Some("com.gopewpew")

val fs2Version = "3.0.5"
val scalaTestVersion = "3.2.9"

libraryDependencies ++= Seq(
  "co.fs2" %% "fs2-core" % fs2Version,
  "co.fs2" %% "fs2-io" % fs2Version,
  "org.scalatest" %% "scalatest" % scalaTestVersion % Test
)
