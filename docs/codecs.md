---
description: Derive zio-json/circe BSON codecs
hide:
  - toc
  - navigation
---

# Codec derivation

To perform any database operation which involves data fetching or updating, it is required to serialize and deserialize data to and from BSON. zio-mongo provides codecs for all the basic types and also provides a way to derive codecs for your own types. To encode and decode case classes and sealed traits, currently zio-mongo can be used with zio-json and circe

## Deriving ZIO-JSON codecs

Dependency

![Maven Central](https://img.shields.io/maven-central/v/com.bilal-fazlani.zio-mongo/zio-mongo_3?color=blue&label=Latest%20Version&style=for-the-badge)

```scala
libraryDependencies +=  "com.bilal-fazlani.zio-mongo" %% "zio-json-codec" % zioMongoVersion
```


You can derive codes for case classes using `derives` keyword

```scala
import com.bilalfazlani.zioMongo.codecs.zioJson.{ given, * }
import zio.json.JsonCodec
import org.bson.types.ObjectId

case class Person(
    _id: ObjectId, 
    name: String, 
    lastName: String, 
    age: Int
  ) derives JsonCodec
```

Another way to derive codecs is manualling invoking a macro

```scala
import com.bilalfazlani.zioMongo.codecs.zioJson.{ given, * }
import zio.json.JsonCodec
import org.bson.types.ObjectId

case class Person(
    _id: ObjectId,
    name: String,
    lastName: String,
    age: Int
) {
  given JsonCodec[Person] = zio.json.DeriveJsonCodec.gen[Person]
}
```

[:simple-github: Example](https://github.com/bilal-fazlani/zio-mongo/tree/main/zio-json-examples/src/main/scala/com/bilalfazlani/zioMongo/example){ .md-button }


## Deriving Circe codecs

Dependency

![Maven Central](https://img.shields.io/maven-central/v/com.bilal-fazlani.zio-mongo/zio-mongo_3?color=blue&label=Latest%20Version&style=for-the-badge)

```scala
libraryDependencies +=  "com.bilal-fazlani.zio-mongo" %% "circe-codec" % zioMongoVersion
```

You can derive circe codecs using `derives` keyword

```scala

```

or by manually invoking a macro

```scala
import org.bson.types.ObjectId
import com.bilalfazlani.zioMongo.codecs.circe.given
import io.circe.Codec.AsObject

case class Person(
    _id: ObjectId,
    name: String,
    lastName: String,
    age: Int
) derives AsObject
```

Alternatively, you can use semi-automatic derivation using `io.circe.generic.semiauto._` import where codecs are needed

```scala
import org.bson.types.ObjectId
import com.bilalfazlani.zioMongo.codecs.circe.given
import io.circe.generic.semiauto._
import io.circe.Codec

case class Person(
    _id: ObjectId,
    name: String,
    lastName: String,
    age: Int
)

object Person {
  given Codec[Person] = deriveCodec[Person]
}
```

When using cirece, you can also use fully automatic derivation using `io.circe.generic.auto._` import where codecs are needed

[:simple-github: Example](https://github.com/bilal-fazlani/zio-mongo/tree/main/circe-examples/src/main/scala/com/bilalfazlani/zioMongo/example){ .md-button }

