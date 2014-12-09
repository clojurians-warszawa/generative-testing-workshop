name := "scalacheck-workshop"

version := "1.0"

scalaVersion := "2.11.4"

resolvers +=
  "Sonatype OSS Snapshots" at "https://oss.sonatype.org/content/repositories/snapshots"

libraryDependencies ++= Seq("org.scalatest" %% "scalatest" % "2.2.1" % "test")

libraryDependencies ++= Seq("org.scalacheck" %% "scalacheck" % "1.12.1" % "test")