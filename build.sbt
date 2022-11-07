import Dependencies._

ThisBuild / scalaVersion        := "3.2.0"
ThisBuild / organization        := "com.bilal-fazlani.zio-mongo"
ThisBuild / sonatypeProfileName := "com.bilal-fazlani"
ThisBuild / organizationName    := "Bilal Fazlani"
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
  .aggregate(
    `zio-mongo`,
    `circe-codec`,
    `circe-examples`,
    `zio-json-codec`,
    `zio-json-examples`,
    tests
  )

lazy val `zio-mongo`: Project = (project in file("zio-mongo"))
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

lazy val `circe-codec`: Project = (project in file("circe-codec"))
  .settings(
    description := "Circe codecs for zio-mongo",
    name        := "circe-codec",
    libraryDependencies ++= Seq(
      Circe.circeParser
    )
  )
  .dependsOn(`zio-mongo`)

lazy val `zio-json-codec`: Project = (project in file("zio-json-codec"))
  .settings(
    description := "ZIO Json codecs for zio-mongo",
    name        := "zio-json-codec",
    libraryDependencies ++= Seq(
      ZioJson.zioJson
    )
  )
  .dependsOn(`zio-mongo`)

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
  .dependsOn(`zio-mongo`, `circe-codec`, `zio-json-codec`)

lazy val `circe-examples` = (project in file("circe-examples"))
  .settings(
    publish / skip := true,
    libraryDependencies ++= Seq(
      Circe.circeGeneric
    )
  )
  .dependsOn(`zio-mongo`, `circe-codec`)

lazy val `zio-json-examples` = (project in file("zio-json-examples"))
  .settings(
    publish / skip := true,
    libraryDependencies ++= Seq(
      ZioJson.zioJson
    )
  )
  .dependsOn(`zio-mongo`, `zio-json-codec`)
