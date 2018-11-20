name := "neo4cats"

version := "0.1"

scalaVersion := "2.12.7"

libraryDependencies += "org.typelevel" %% "cats-effect" % "1.0.0"
libraryDependencies += "co.fs2" %% "fs2-core" % "1.0.0"
libraryDependencies += "org.neo4j.driver" % "neo4j-java-driver" % "1.7.0"
libraryDependencies += "org.scala-lang.modules" %% "scala-java8-compat" % "0.9.0"
libraryDependencies += "org.scalamock" %% "scalamock" % "4.1.0" % Test
libraryDependencies += "org.scalatest" %% "scalatest" % "3.0.4" % Test