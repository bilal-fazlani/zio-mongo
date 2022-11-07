import scala.util.Try
import Dependencies._

ThisBuild / scalaVersion     := "3.2.0"
ThisBuild / organization     := "com.bilal-fazlani"
ThisBuild / organizationName := "Bilal Fazlani"
ThisBuild / testFrameworks += new TestFramework("zio.test.sbt.ZTestFramework")
ThisBuild / scmInfo := Some(
  ScmInfo(url("https://github.com/bilal-fazlani/zio-mongo"), "https://github.com/bilal-fazlani/zio-mongo.git")
)
ThisBuild / developers := List(
  Developer(
    "bilal-fazlani",
    "Bilal Fazlani",
    "bilal.m.fazlani@gmail.com",
    url("https://bilal-fazlani.com")
  )
)
ThisBuild / licenses := List("Apache-2.0" -> url("http://www.apache.org/licenses/LICENSE-2.0"))
ThisBuild / homepage := Some(url("https://github.com/bilal-fazlani/zio-mongo"))

lazy val zioMongoRoot = (project in file("."))
  .settings(
    publish / skip := true
  )
  .aggregate(zioMongo, zioMongoCirce, zioJsonMongo, circeExamples, zioJsonExamples, tests)

lazy val zioMongo: Project = (project in file("zio-mongo"))
  .settings(
    description := "ZIO wrapper for MongoDB Reactive Streams Java Driver",
    name        := "zio-mongo",
    libraryDependencies ++= Seq(
      mongoScala,
      mongodbDriverStreams,
      logback,
      zio,
      zioStreams,
      zstreamInterops
    )
  )

lazy val zioMongoCirce: Project = (project in file("zio-mongo-circe"))
  .settings(
    description := "Circe codecs for zio-mongo",
    name        := "zio-mongo-circe",
    libraryDependencies ++= Seq(
      Circe.circeParser
    )
  )
  .dependsOn(zioMongo)

lazy val zioJsonMongo: Project = (project in file("zio-json-mongo"))
  .settings(
    description := "ZIO Json codecs for zio-mongo",
    name        := "zio-json-mongo",
    libraryDependencies ++= Seq(
      ZioJson.zioJson
    )
  )
  .dependsOn(zioMongo)

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
  .dependsOn(zioMongo, zioMongoCirce)

lazy val circeExamples = (project in file("zio-mongo-circe-examples"))
  .settings(
    publish / skip := true,
    libraryDependencies ++= Seq(
      Circe.circeGeneric
    )
  )
  .dependsOn(zioMongo, zioMongoCirce)

lazy val zioJsonExamples = (project in file("zio-json-mongo-examples"))
  .settings(
    publish / skip := true,
    libraryDependencies ++= Seq(
      ZioJson.zioJson
    )
  )
  .dependsOn(zioMongo, zioJsonMongo)
