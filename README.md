<img src="media/logo-medium.png" style="width: 18rem;">

# ZIO-MONGO

![Maven Central](https://img.shields.io/maven-central/v/com.bilal-fazlani.zio-mongo/zio-mongo_3?color=blue&label=Latest%20Version&style=for-the-badge)

ZIO wrapper for [MongoDB Java Reactive Streams](https://www.mongodb.com/docs/drivers/reactive-streams/)

> With support for ZIO JSON and Circe codecs

### Dependencies

Supports Scala 3 and ZIO 2

```scala
libraryDependencies += "com.bilal-fazlani.zio-mongo" %% "zio-mongo" % "<VERSION>"
```

for zio-json codecs

```scala
libraryDependencies += "com.bilal-fazlani.zio-mongo" %% "zio-json-codec" % "<VERSION>"
```

for circe codecs

```scala
libraryDependencies += "com.bilal-fazlani.zio-mongo" %% "circe-codec" % "<VERSION>"
```

### Documentation

```scala
import com.bilalfazlani.zioMongo.*
import com.bilalfazlani.zioMongo.codecs.circe.given
import io.circe.generic.auto.*
import org.bson.types.ObjectId
import org.mongodb.scala.bson.ObjectId
import org.mongodb.scala.model.Filters.equal
import org.mongodb.scala.model.Updates.set
import zio.*

case class Person(_id: ObjectId, name: String, lastName: String, age: Int)

object CaseClassExample extends ZIOAppDefault {

  val persons: Seq[Person] = Seq(
    Person(ObjectId(), "Charles", "Babbage", 34),
    Person(ObjectId(), "George", "Boole", 19),
    Person(ObjectId(), "Gertrude", "Blanch", 74),
    Person(ObjectId(), "Grace", "Hopper", 14),
    Person(ObjectId(), "Ida", "Rhodes", 30),
    Person(ObjectId(), "Jean", "Bartik", 22),
    Person(ObjectId(), "John", "Backus", 56),
    Person(ObjectId(), "Lucy", "Sanders", 51),
    Person(ObjectId(), "Tim", "Berners Lee", 46),
    Person(ObjectId(), "Zaphod", "Beeblebrox", 15)
  )

  override def run = for {
    client   <- MongoZioClient("mongodb://localhost:27017")
    database <- client.getDatabase("mydb", JCodec[Person])
    col      <- database.getCollection[Person]("test")
    person     = Person(ObjectId(), "bilal", "f", 1)
    insertR <- col.insertOne(person)
    first   <- col.find().runHead
    _       <- zio.Console.printLine(first)
    _       <- col.insertMany(persons)
    _       <- zio.Console.printLine("5.....")
    _       <- col.find(equal("name", "Ida")).runHead
    _       <- col.updateOne(equal("name", "Jean"), set("lastName", "Bannour"))
    _       <- col.deleteOne(equal("name", "Zaphod"))
    count   <- col.countDocuments()
    person  <- col.find(equal("name", "Jean")).runHead
    _       <- zio.Console.printLine(s"Persons count: $count")
    _       <- zio.Console.printLine(s"The updated person with name Jean is: $person")
  } yield ()
}
```
