---
hide:
  - toc
---

# Deriving Circe codecs

Dependency

![Maven Central](https://img.shields.io/maven-central/v/com.bilal-fazlani.zio-mongo/zio-mongo_3?color=blue&label=Latest%20Version&style=for-the-badge)

```scala
libraryDependencies +=  "com.bilal-fazlani.zio-mongo" %% "circe-codec" % zioMongoVersion
```

You can derive circe codecs using `derives` keyword

```scala
import org.bson.types.ObjectId
import io.circe.Codec
import com.bilalfazlani.zioMongo.codecs.circe.{given, *}

case class Person(
  _id: ObjectId, 
  name: String, 
  lastName: String, 
  age: Int
) derives Codec
```

or by manually invoking a macro

```scala
import org.bson.types.ObjectId
import io.circe.Codec
import com.bilalfazlani.zioMongo.codecs.circe.given
import io.circe.generic.semiauto.deriveCodec

case class Person(
  _id: ObjectId, 
  name: String, 
  lastName: String, 
  age: Int
)

object Person:
  given Codec[Person] = deriveCodec
```

Alternatively,you can use fully automatic derivation using `io.circe.generic.auto._` import where codecs are needed

[:simple-github: Example](https://github.com/bilal-fazlani/zio-mongo/tree/main/circe-examples/src/main/scala/com/bilalfazlani/zioMongo/example){ .md-button }

