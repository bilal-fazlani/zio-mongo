package com.bilalfazlani
package subscriptions

import com.bilalfazlani.MongoTestClient.mongoTestClient
import com.bilalfazlani.Person
import com.bilalfazlani.circe.given
import org.bson.codecs.configuration.CodecRegistries.{ fromProviders, fromRegistries }
import org.mongodb.scala.MongoClient.DEFAULT_CODEC_REGISTRY
import org.mongodb.scala.model.Filters
import zio.{ Duration, ExecutionStrategy }
import zio.test.Assertion.equalTo
import zio.test.TestEnvironment
import zio.test.{ TestAspect, ZIOSpecDefault, ZSpec, assertM }
import com.bilalfazlani.CodecRegistry
import io.circe.generic.auto.*
import zio.Chunk
import zio.Random

object DistinctSubscriptionSpec extends ZIOSpecDefault {

  val mongoClient = mongoTestClient()

  val codecRegistry = fromRegistries(CodecRegistry[Person], DEFAULT_CODEC_REGISTRY)

  val database = mongoClient.getDatabase("mydb").map(_.withCodecRegistry(codecRegistry))

  def collection = for {
    db   <- database
    name <- randomString(10)
    coll <- db.getCollection[Person](name)
  } yield coll

  override def aspects: Chunk[TestAspect[Nothing, TestEnvironment, Nothing, Any]] =
    Chunk(TestAspect.executionStrategy(ExecutionStrategy.Sequential), TestAspect.timeout(Duration.fromMillis(30000)))

  override def spec: ZSpec[TestEnvironment, Any] = suite("DistinctSubscriptionSpec")(
    // distinctDocuments(),
    // distinctFirstDocuments(),
    // filterDistinctDocuments(),
    // close()
  )

  def distinctDocuments() = {
    val names = for {
      col <- collection
      _ <- col.insertMany(
        Seq(
          Person("John", 20),
          Person("Carmen", 40),
          Person("John", 15),
          Person("Yasmin", 30)
        )
      )
      doc <- col.distinct[String]("name").fetch.runCollect
    } yield doc

    test("Get distinct Persons by name") {
      assertM(names)(equalTo(Seq("John", "Carmen", "Yasmin")))
    }
  }

  def distinctFirstDocuments() = {
    val names = for {
      col <- collection
      doc <- col.distinct[String]("name").first().fetch
    } yield doc

    test("Get first person Persons by name") {
      assertM(names)(equalTo("John"))
    }
  }

  def filterDistinctDocuments() = {
    val names = for {
      col <- collection
      doc <- col.distinct[String]("name").filter(Filters.gt("age", 30)).fetch.runCollect
    } yield doc

    test("Get filtered persons with age greater than 30") {
      assertM(names)(equalTo(Seq("Carmen")))
    }
  }

  def close() =
    test("Close database and clean") {
      val close = for {
        col <- collection
        _   <- col.drop()
        _   <- mongoClient.pureClose()

      } yield ()
      assertM(close)(equalTo(()))
    }

}
