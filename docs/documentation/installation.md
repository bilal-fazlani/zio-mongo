---
hide:
  - toc
---

# Installation

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