import Dependencies._

ThisBuild / scalaVersion     := "3.1.2"
ThisBuild / version          := "0.0.2"
ThisBuild / organization     := "com.bilalfazlani"
ThisBuild / organizationName := "Bilal Fazlani"
ThisBuild / description      := "ZIO wrapper for MongoDB Reactive Streams Java Driver"
ThisBuild / scmInfo          := Some(ScmInfo(url("https://github.com/bilal-fazlani/zio-mongo"), "https://github.com/bilal-fazlani/zio-mongo.git"))
ThisBuild / developers       := List(Developer("", "bilal-fazlani", "bilal.m.fazlani@gmail.com", url("https://github.com/bilal-fazlani")))
ThisBuild / licenses         := List("Apache-2.0" -> url("http://www.apache.org/licenses/LICENSE-2.0"))
ThisBuild / homepage         := Some(url("https://github.com/bilal-fazlani/zio-mongo"))

lazy val ziomongo = (project in file("."))
  .settings(
    publish / skip := true
  )
  .aggregate(zioCore, examples)

lazy val zioCore = (project in file("zio-core"))
  .settings(
    name := "ziomongo",
    libraryDependencies ++= Seq(
        mongoScala,
        mongodbDriverStreams,
        logback,
        zio,
        zioStreams,
        zioMagnoliaTest % Test,
        zioTestSbt % Test,
        zioTest % Test,
        scalaTest % Test
      ),
    testFrameworks += new TestFramework("zio.test.sbt.ZTestFramework"),
    Test / testOptions  ++= Seq(Tests.Setup(() => MongoEmbedded.start), Tests.Cleanup(() => MongoEmbedded.stop)),
    Test / parallelExecution := false
  )

lazy val examples = (project in file("zio-examples"))
  .settings(
    publish / skip := true
  )
  .dependsOn(zioCore)


