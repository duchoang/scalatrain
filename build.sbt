organization := "com.typesafe.training"

name := "scala-train"

version := "3.0.0"

scalaVersion := Version.scala

// The Typesafe repository
//resolvers += Resolver.TypesafeRepositoryRoot

libraryDependencies ++= Dependencies.scalaTrain

scalacOptions ++= List(
  "-unchecked",
  "-deprecation",
  "-language:_",
  "-target:jvm-1.7",
  "-encoding", "UTF-8"
)

initialCommands in console := "import com.typesafe.training.scalatrain._"

initialCommands in (Test, console) := (initialCommands in console).value + ",TestData._"
