import sbt._

object Version {
  val scala        = "2.11.6"
  val scalaParsers = "1.0.3"
  val scalaTest    = "2.2.4"
  val playJson     = "2.3.8"
}

object Library {
  val scalaParsers = "org.scala-lang.modules" %% "scala-parser-combinators" % Version.scalaParsers
  val scalaTest    = "org.scalatest"          %% "scalatest"                % Version.scalaTest
  val playJson     = "com.typesafe.play"      %% "play-json"                % Version.playJson
  val joda         = "org.joda"               % "joda-convert"             % "1.6"
  val ws           = "com.typesafe.play"	    %% "play-ws" 	    % "2.4.0-M1" 			% "compile"
}

object Dependencies {

  import Library._

  val scalaTrain = List(
    scalaParsers,
    scalaTest % "test",
    playJson,
    joda,
    ws
  )
}
