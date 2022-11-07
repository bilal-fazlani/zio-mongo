package com.bilalfazlani.zioMongo
package example

import circe.given
import io.circe.generic.auto.*
import org.bson.types.ObjectId
import org.mongodb.scala.bson.ObjectId
import org.mongodb.scala.model.Filters.equal
import org.mongodb.scala.model.Updates.set
import zio.*
import zio.Console.*
import zio.stream.ZSink

object CirceExample extends ZIOAppDefault {

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
    person = Person(ObjectId(), "bilal", "f", 1)
    insertR <- col.insertOne(person)
    first   <- col.find().runHead
    _       <- printLine(first)
    _       <- col.insertMany(persons)
    _       <- printLine("5.....")
    _       <- col.find(equal("name", "Ida")).runHead
    _       <- col.updateOne(equal("name", "Jean"), set("lastName", "Bannour"))
    _       <- col.deleteOne(equal("name", "Zaphod"))
    count   <- col.countDocuments()
    person  <- col.find(equal("name", "Jean")).runHead
    _       <- printLine(s"Persons count: $count")
    _       <- printLine(s"The updated person with name Jean is: $person")
  } yield ()
}
