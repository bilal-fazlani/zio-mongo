package com.bilalfazlani
package subscriptions

import com.mongodb.client.model.Accumulators.push
import com.bilalfazlani.{ Company, FundingRound }
import com.bilalfazlani.MongoTestClient.mongoTestClient
import com.bilalfazlani.circe.given
import org.bson.codecs.configuration.CodecRegistries.{ fromProviders, fromRegistries }
import org.mongodb.scala.MongoClient.DEFAULT_CODEC_REGISTRY
import org.mongodb.scala.bson.{ BsonArray, BsonInt32, BsonInt64, BsonString, ObjectId }
import org.mongodb.scala.bson.collection.immutable.Document
import org.mongodb.scala.model.{ Aggregates, Filters, Projections }
import zio.{ Duration, ExecutionStrategy, ZIO }
import zio.test.Assertion.equalTo
import zio.test.{ TestAspect, ZIOSpecDefault, ZSpec, assertM }
import zio.test.ZIOSpecDefault
import zio.test.TestEnvironment
import com.bilalfazlani.CodecRegistry
import io.circe.generic.auto.*
import zio.Chunk
import zio.Random
import zio.stream.ZStream
import zio.interop.reactivestreams.*

val companies = List(
    Company(
      new ObjectId(),
      "Facebook",
      "social",
      2004,
      "Social Network",
      List(FundingRound(2004, 8500000), FundingRound(2005, 2800000), FundingRound(2006, 28700000))
    ),
    Company(
      new ObjectId(),
      "Veveo",
      "private",
      2004,
      "Conversational interfaces",
      List(FundingRound(2004, 780000), FundingRound(2005, 990000), FundingRound(2006, 29870000))
    ),
    Company(
      new ObjectId(),
      "AddThis",
      "social",
      2004,
      "Social Network",
      List(FundingRound(2004, 80000), FundingRound(2005, 2110000), FundingRound(2006, 89700000))
    ),
    Company(
      new ObjectId(),
      "Veoh",
      "social",
      2004,
      "Social Network",
      List(FundingRound(2004, 50000), FundingRound(2005, 90000), FundingRound(2006, 9000005))
    ),
    Company(
      new ObjectId(),
      "Pando Networks",
      "social",
      2004,
      "Social Network",
      List(FundingRound(2004, 78000), FundingRound(2005, 1110000), FundingRound(2005, 78900000))
    ),
    Company(
      new ObjectId(),
      "Afiniti Ltd",
      "private",
      2005,
      "Artificial intelligence",
      List(FundingRound(2005, 60000), FundingRound(2006, 890000), FundingRound(2007, 29900000))
    ),
    Company(
      new ObjectId(),
      "LucidEra",
      "private",
      2005,
      " business intelligence",
      List(FundingRound(2005, 84440000), FundingRound(2006, 21210000), FundingRound(2007, 234350000))
    ),
    Company(
      new ObjectId(),
      "gamerDNA",
      "social",
      2006,
      "video game",
      List(FundingRound(2006, 898900), FundingRound(2007, 660000), FundingRound(2008, 35400000))
    ),
    Company(
      new ObjectId(),
      "Sunamp",
      "private",
      2006,
      "Sunamp",
      List(FundingRound(2006, 60000), FundingRound(2007, 2880000), FundingRound(2008, 29990000))
    ),
    Company(
      new ObjectId(),
      "Fiksu",
      "social",
      2008,
      "Social Network",
      List(FundingRound(2008, 70000), FundingRound(2009, 550000), FundingRound(2010, 342340000))
    )
  )

object TestObj extends zio.ZIOAppDefault {
  val mongoClient = mongoTestClient()

  val codecRegistry = fromRegistries(CodecRegistry[Company], DEFAULT_CODEC_REGISTRY)

  override def run = for {
    database <- mongoClient.getDatabase("testdb").map(_.withCodecRegistry(codecRegistry))
    collection <- database.getCollection[Company]("companies")
    csp <- collection.watch().fetch.runCollect
    // ff = d
  } yield ()
}  

object AggregateSubscriptionSpec extends ZIOSpecDefault {

  val companies = List(
    Company(
      new ObjectId(),
      "Facebook",
      "social",
      2004,
      "Social Network",
      List(FundingRound(2004, 8500000), FundingRound(2005, 2800000), FundingRound(2006, 28700000))
    ),
    Company(
      new ObjectId(),
      "Veveo",
      "private",
      2004,
      "Conversational interfaces",
      List(FundingRound(2004, 780000), FundingRound(2005, 990000), FundingRound(2006, 29870000))
    ),
    Company(
      new ObjectId(),
      "AddThis",
      "social",
      2004,
      "Social Network",
      List(FundingRound(2004, 80000), FundingRound(2005, 2110000), FundingRound(2006, 89700000))
    ),
    Company(
      new ObjectId(),
      "Veoh",
      "social",
      2004,
      "Social Network",
      List(FundingRound(2004, 50000), FundingRound(2005, 90000), FundingRound(2006, 9000005))
    ),
    Company(
      new ObjectId(),
      "Pando Networks",
      "social",
      2004,
      "Social Network",
      List(FundingRound(2004, 78000), FundingRound(2005, 1110000), FundingRound(2005, 78900000))
    ),
    Company(
      new ObjectId(),
      "Afiniti Ltd",
      "private",
      2005,
      "Artificial intelligence",
      List(FundingRound(2005, 60000), FundingRound(2006, 890000), FundingRound(2007, 29900000))
    ),
    Company(
      new ObjectId(),
      "LucidEra",
      "private",
      2005,
      " business intelligence",
      List(FundingRound(2005, 84440000), FundingRound(2006, 21210000), FundingRound(2007, 234350000))
    ),
    Company(
      new ObjectId(),
      "gamerDNA",
      "social",
      2006,
      "video game",
      List(FundingRound(2006, 898900), FundingRound(2007, 660000), FundingRound(2008, 35400000))
    ),
    Company(
      new ObjectId(),
      "Sunamp",
      "private",
      2006,
      "Sunamp",
      List(FundingRound(2006, 60000), FundingRound(2007, 2880000), FundingRound(2008, 29990000))
    ),
    Company(
      new ObjectId(),
      "Fiksu",
      "social",
      2008,
      "Social Network",
      List(FundingRound(2008, 70000), FundingRound(2009, 550000), FundingRound(2010, 342340000))
    )
  )

  val mongoClient = mongoTestClient()

  val codecRegistry = fromRegistries(CodecRegistry[Company], CodecRegistry[FundingRound], DEFAULT_CODEC_REGISTRY)

  val database = mongoClient.getDatabase("mydb").map(_.withCodecRegistry(codecRegistry))

  val collection = for {
    db <- database
    name = "collection-AggregateSubscriptionSpec"
    coll <- db.getCollection[Company](name)
  } yield coll

  override def aspects: Chunk[TestAspect[Nothing, TestEnvironment, Nothing, Any]] =
    Chunk(TestAspect.executionStrategy(ExecutionStrategy.Sequential), TestAspect.timeout(Duration.Infinity))

  override def spec: ZSpec[TestEnvironment, Any] = suite("AggregateSubscriptionSpec")(
    initialCount(),
    insertCompanies(),
    aggregateSortedCompanies(),
    aggregateCompaniesByGroup(),
    // aggregateWithUnwind(),
    // closeConnection()
  )

  def initialCount(): ZSpec[Any, Throwable] = {
    val count = for {
      col   <- collection
      count <- col.countDocuments()
    } yield count

    test("Count Documents") {
      assertM(count)(equalTo(0L))
    }
  }

  def insertCompanies(): ZSpec[Any, Throwable] = {
    val count = for {
      col   <- collection
      _     <- col.insertMany(companies)
      count <- col.countDocuments()
    } yield count

    test("Insert Documents") {
      assertM(count)(equalTo(10L))
    }
  }

  def aggregateSortedCompanies(): ZSpec[Any, Throwable] = {
    val aggregatedResult = for {
      col <- collection
      res <- col
        .aggregate(
          Seq(
            Aggregates.`match`(Filters.equal("founded_year", 2004)),
            Aggregates.sort(Filters.equal("name", 1)),
            Aggregates.limit(2),
            Aggregates.project(
              Projections.fields(
                Projections.excludeId(),
                Projections.include("name")
              )
            )
          )
        )
        .fetch.runCollect
    } yield res

    test("Find sorted company names founded in 2004 and limited to two") {
      assertM(aggregatedResult)(
        equalTo(
          Seq(
            Document("name" -> BsonString("AddThis")),
            Document("name" -> BsonString("Facebook"))
          )
        )
      )
    }
  }

  def aggregateCompaniesByGroup(): ZSpec[Any, Throwable] = {
    val aggregatedResult: ZIO[Any, Throwable, Iterable[Document]] = for {
      col <- collection
      res <- col
        .aggregate(
          Seq(
            Aggregates.`match`(Filters.gte("founded_year", 2004)),
            Aggregates.group("$founded_year", push("companies", "$name")),
            Aggregates.sort(Filters.equal("founded_year", 1)),
            Aggregates.limit(2)
          )
        )
        .fetch.runCollect
    } yield res
    test("Find sorted company names founded in 2004 and limited to two") {
      assertM(aggregatedResult)(
        equalTo(
          Seq(
            Document("_id" -> BsonInt32(2008), "companies" -> BsonArray(BsonString("Fiksu"))),
            Document("_id" -> BsonInt32(2006), "companies" -> BsonArray(BsonString("gamerDNA"), BsonString("Sunamp"))),
          )
        )
      )
    }
  }

  def aggregateWithUnwind(): ZSpec[Any, Throwable] = {
    val aggregatedResult: ZIO[Any, Throwable, Iterable[Document]] = for {
      col <- collection
      result <- col
        .aggregate(
          Seq(
            Aggregates.`match`(Filters.equal("founded_year", 2004)),
            Aggregates.unwind("$funding_rounds"),
            Aggregates.limit(3),
            Aggregates.project(
              Projections.fields(
                Projections.excludeId(),
                Projections.include(Seq("name", "funding_rounds.year", "funding_rounds.amount"): _*)
              )
            )
          )
        )
        .fetch.runCollect
        .debug("fetched data")
    } yield result

    test("Find company names , funding rounds year and funding rounds amount limited to 3") {
      assertM(aggregatedResult)(
        equalTo(
          Seq(
            Document(
              "name"           -> BsonString("Facebook"),
              "funding_rounds" -> Document("year" -> BsonInt32(2004), "amount" -> BsonInt64(8500000))
            ),
            Document(
              "name"           -> BsonString("Facebook"),
              "funding_rounds" -> Document("year" -> BsonInt32(2005), "amount" -> BsonInt64(2800000))
            ),
            Document(
              "name"           -> BsonString("Facebook"),
              "funding_rounds" -> Document("year" -> BsonInt32(2006), "amount" -> BsonInt64(28700000))
            )
          )
        )
      )
    }
  }

  def closeConnection() =
    test("Close and clean database") {
      val close = for {
        col <- collection
        _   <- col.drop()
        _   <- mongoClient.pureClose()

      } yield ()
      assertM(close)(equalTo(()))
    }

}
