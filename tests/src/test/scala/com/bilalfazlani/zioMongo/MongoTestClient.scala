package com.bilalfazlani.zioMongo

import com.bilalfazlani.zioMongo.MongoZioClient.createMongoClient
import com.mongodb.{ ConnectionString, MongoClientSettings }

object MongoTestClient {

  lazy val urlConfig =
    MongoClientSettings.builder().applyConnectionString(new ConnectionString("mongodb://localhost:27017")).build()

  def mongoTestClient() = createMongoClient(urlConfig, None)
}
