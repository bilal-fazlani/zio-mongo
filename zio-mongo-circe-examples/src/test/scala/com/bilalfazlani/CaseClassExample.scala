package com.bilalfazlani.zioMongo
package example

import com.bilalfazlani.zioMongo.circe.given
import io.circe.Codec
import io.circe.Decoder
import io.circe.Encoder
import io.circe.Json
import io.circe.JsonObject
import io.circe.generic.auto.*
import io.circe.syntax.*
import org.bson.Document
import org.bson.codecs.configuration.CodecProvider
import org.bson.codecs.configuration.CodecRegistries.fromCodecs
import org.bson.codecs.configuration.CodecRegistries.fromRegistries
import org.bson.codecs.configuration.{ CodecRegistry => JCodecRegistry }
import org.mongodb.scala.MongoClient.DEFAULT_CODEC_REGISTRY
import org.mongodb.scala.bson.ObjectId
import org.mongodb.scala.model.Filters.equal
import org.mongodb.scala.model.Updates.set
import zio.ExitCode
import zio.Task
import zio.URIO
import zio.ZIO
import zio.stream.ZSink

import scala.util.Try

import collection.convert.ImplicitConversions.*

case class Person(name: String, lastName: String, age: Int)

object CaseClassExample extends zio.ZIOAppDefault {

  val persons: Seq[Person] = Seq(
    Person("Charles", "Babbage", 34),
    Person("George", "Boole", 19),
    Person("Gertrude", "Blanch", 74),
    Person("Grace", "Hopper", 14),
    Person("Ida", "Rhodes", 30),
    Person("Jean", "Bartik", 22),
    Person("John", "Backus", 56),
    Person("Lucy", "Sanders", 51),
    Person("Tim", "Berners Lee", 46),
    Person("Zaphod", "Beeblebrox", 15)
  )

  given JCodecRegistry = DEFAULT_CODEC_REGISTRY

  val customRegistry: JCodecRegistry = fromCodecs(JCodec[Person])

  val codecRegistry = fromRegistries(DEFAULT_CODEC_REGISTRY, customRegistry)

  val app = for {
    client   <- MongoZioClient("mongodb://localhost:27017")
    database <- client.getDatabase("mydb").map(_.withCodecRegistry(codecRegistry))
    col      <- database.getCollection[Person]("test")
    person     = Person("bilal", "f", 1)
    personJson = person.asJson
    insertR <- col.insertOne(person)
    first   <- col.find.runHead
    _       <- zio.Console.printLine(first)
    _       <- col.insertMany(persons)
    _       <- zio.Console.printLine("5.....")
    _       <- col.find.runHead
    _       <- col.find(equal("name", "Ida")).runHead
    _       <- col.updateOne(equal("name", "Jean"), set("lastName", "Bannour"))
    _       <- col.deleteOne(equal("name", "Zaphod"))
    count   <- col.countDocuments()
    person  <- col.find(equal("name", "Jean")).runHead
    _       <- zio.Console.printLine(s"Persons count: $count")
    _       <- zio.Console.printLine(s"The updated person with name Jean is: $person")
  } yield ()

  override def run = app
}
