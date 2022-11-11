---
description: Learn how to add zio-mongo to your project, and how to use it with SBT.
hide:
  - toc
---

# Installation

![Maven Central](https://img.shields.io/maven-central/v/com.bilal-fazlani.zio-mongo/zio-mongo_3?color=blue&label=Latest%20Version&style=for-the-badge)

Add the following to your `build.sbt` file:

```scala
val zioMongo = "com.bilal-fazlani.zio-mongo"
val zioMongoVersion = "<ADD VERSION HERE>"

libraryDependencies ++= Seq(
  zioMongo %% "zio-mongo" % zioMongoVersion,

  // for circe codecs
  zioMongo %% "circe-codec" % zioMongoVersion,
  // OR for zio-json codecs
  zioMongo %% "zio-json-codec" % zioMongoVersion, 
)
```