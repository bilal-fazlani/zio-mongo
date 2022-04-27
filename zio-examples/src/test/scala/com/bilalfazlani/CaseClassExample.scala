package com.bilalfazlani

import com.bilalfazlani.MongoZioClient
import zio.{ ExitCode, Task, URIO, ZIO }
import org.bson.codecs.configuration.CodecRegistries.fromRegistries
import org.mongodb.scala.MongoClient.DEFAULT_CODEC_REGISTRY
import org.mongodb.scala.bson.ObjectId
import org.mongodb.scala.model.Filters.equal
import org.mongodb.scala.model.Updates.set
import org.bson.codecs.configuration.CodecProvider
import com.bilalfazlani.circe.given
import com.bilalfazlani.MongoCodecProvider
import io.circe.generic.auto.*
import io.circe.Encoder
import io.circe.Decoder
import io.circe.JsonObject
import io.circe.Json
import scala.util.Try
import io.circe.Codec

object CaseClassExample extends zio.ZIOAppDefault {

  case class Person(_id: ObjectId, name: String, lastName: String, age: Int)

  val persons: Seq[Person] = Seq(
    Person(new ObjectId(), "Charles", "Babbage", 34),
    Person(new ObjectId(), "George", "Boole", 19),
    Person(new ObjectId(), "Gertrude", "Blanch", 74),
    Person(new ObjectId(), "Grace", "Hopper", 14),
    Person(new ObjectId(), "Ida", "Rhodes", 30),
    Person(new ObjectId(), "Jean", "Bartik", 22),
    Person(new ObjectId(), "John", "Backus", 56),
    Person(new ObjectId(), "Lucy", "Sanders", 51),
    Person(new ObjectId(), "Tim", "Berners Lee", 46),
    Person(new ObjectId(), "Zaphod", "Beeblebrox", 15)
  )

  val codecRegistry = fromRegistries(CodecRegistry[Person], DEFAULT_CODEC_REGISTRY)

  val app = ZIO.scoped(for {
    client   <- MongoZioClient("mongodb://localhost:27017")
    database <- client.getDatabase("mydb").map(_.withCodecRegistry(codecRegistry))
    col      <- database.getCollection[Person]("test")
    _        <- col.insertMany(persons)
    _        <- col.find().first().fetch
    _        <- col.find(equal("name", "Ida")).first().fetch
    _        <- col.updateOne(equal("name", "Jean"), set("lastName", "Bannour"))
    _        <- col.deleteOne(equal("name", "Zaphod"))
    count    <- col.countDocuments()
    person   <- col.find(equal("name", "Jean")).first().headOption
    _        <- zio.Console.printLine(s"Persons count: $count")
    _        <- zio.Console.printLine(s"The updated person with name Jean is: $person")
  } yield ())

  override def run = app
}
