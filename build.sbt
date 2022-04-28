import Dependencies._

ThisBuild / scalaVersion     := "3.1.2"
ThisBuild / version          := "0.1.0-SNAPSHOT"
ThisBuild / organization     := "com.bilalfazlani"
ThisBuild / organizationName := "Bilal Fazlani"
ThisBuild / testFrameworks += new TestFramework("zio.test.sbt.ZTestFramework")
ThisBuild / description := "ZIO wrapper for MongoDB Reactive Streams Java Driver"
ThisBuild / scmInfo := Some(
  ScmInfo(url("https://github.com/bilal-fazlani/zio-mongo"), "https://github.com/bilal-fazlani/zio-mongo.git")
)
ThisBuild / developers := List(
  Developer("", "bilal-fazlani", "bilal.m.fazlani@gmail.com", url("https://github.com/bilal-fazlani"))
)
ThisBuild / licenses := List("Apache-2.0" -> url("http://www.apache.org/licenses/LICENSE-2.0"))
ThisBuild / homepage := Some(url("https://github.com/bilal-fazlani/zio-mongo"))

lazy val ziomongo = (project in file("."))
  .settings(
    publish / skip := true
  )
  .aggregate(zioCore, ziomongoCirce, examples, tests)

lazy val zioCore: Project = (project in file("zio-core"))
  .settings(
    name := "ziomongo",
    libraryDependencies ++= Seq(
      mongoScala,
      mongodbDriverStreams,
      logback,
      zio,
      zioStreams,
      zstreamInterops
    )
  )

lazy val ziomongoCirce: Project = (project in file("ziomongo-circe"))
  .settings(
    name := "ziomongo-circe",
    libraryDependencies ++= Seq(
      Circe.circeParser
    )
  )
  .dependsOn(zioCore)

lazy val tests: Project = (project in file("tests"))
  .settings(
    name           := "tests",
    publish / skip := true,
    libraryDependencies ++= Seq(
      Circe.circeGeneric % Test,
      zioMagnoliaTest    % Test,
      zioTestSbt         % Test,
      zioTest            % Test,
      scalaTest          % Test
    ),
    Test / testOptions ++= Seq(Tests.Setup(() => MongoEmbedded.start), Tests.Cleanup(() => MongoEmbedded.stop)),
    Test / parallelExecution := false
  )
  .dependsOn(zioCore, ziomongoCirce)

lazy val examples = (project in file("zio-examples"))
  .settings(
    publish / skip := true,
    libraryDependencies ++= Seq(
      Circe.circeGeneric
    )
  )
  .dependsOn(zioCore, ziomongoCirce)
