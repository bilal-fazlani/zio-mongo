import sbt._

object Dependencies {

  lazy val mongoVersion     = "4.6.1"
  lazy val zioVersion       = "2.0.0"
  lazy val scalaTestVersion = "3.2.12"
  lazy val scalaMockVersion = "4.3.0"
  lazy val logbackVersion   = "1.1.3"

  lazy val mongoScala = ("org.mongodb.scala" %% "mongo-scala-driver" % mongoVersion).cross(CrossVersion.for3Use2_13)
  lazy val mongodbDriverStreams =
    "org.mongodb" % "mongodb-driver-reactivestreams" % mongoVersion
  lazy val zio             = "dev.zio"       %% "zio"                         % zioVersion
  lazy val zioStreams      = "dev.zio"       %% "zio-streams"                 % zioVersion
  lazy val zstreamInterops = "dev.zio"       %% "zio-interop-reactivestreams" % "2.0.0-RC7"
  lazy val logback         = "ch.qos.logback" % "logback-classic"             % logbackVersion % Test

  //Test
  lazy val scalaTest       = "org.scalatest" %% "scalatest"         % scalaTestVersion
  lazy val zioTest         = "dev.zio"       %% "zio-test"          % zioVersion
  lazy val zioTestSbt      = "dev.zio"       %% "zio-test-sbt"      % zioVersion
  lazy val zioMagnoliaTest = "dev.zio"       %% "zio-test-magnolia" % zioVersion

}

object Circe {
  val circeVersion      = "0.15.0-M1"
  lazy val circeGeneric = "io.circe" %% "circe-generic" % circeVersion
  lazy val circeParser  = "io.circe" %% "circe-parser"  % circeVersion
}
