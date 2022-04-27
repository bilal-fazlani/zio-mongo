package com.bilalfazlani

import com.mongodb.{ConnectionString, MongoClientSettings}
import com.bilalfazlani.MongoZioClient.createMongoClient

object MongoTestClient {

  lazy val urlConfig =
    MongoClientSettings.builder().applyConnectionString(new ConnectionString("mongodb://localhost:27017")).build()

  def mongoTestClient() = createMongoClient(urlConfig, None)
}
