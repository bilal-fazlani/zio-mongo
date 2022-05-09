package com.bilalfazlani

import com.mongodb.{ ClientSessionOptions, ConnectionString, MongoClientSettings, MongoDriverInformation }
import com.mongodb.reactivestreams.client.{ ClientSession, MongoClients }
import com.bilalfazlani.DefaultHelper.MapTo
import org.bson.codecs.configuration.CodecRegistries.fromRegistries
import org.bson.codecs.configuration.CodecRegistry
import org.bson.conversions.Bson
import org.mongodb.scala.bson.collection.immutable.Document
import zio.{ IO, Task, ZIO }
import zio.interop.reactivestreams.*
import scala.jdk.CollectionConverters._
import java.io.Closeable
import scala.reflect.ClassTag

case class MongoZioClient(private val wrapped: JavaMongoClient) extends Closeable {

  /**
    * Creates a client session.
    */
  def startSession() =
    wrapped.startSession().toZIO

  /**
    * Creates a client session.
    */
  def startSession(options: ClientSessionOptions) =
    wrapped.startSession(options).toZIO

  /**
    * Gets the database with the given name.
    */
  def getDatabase(name: String): Task[MongoZioDatabase] = ZIO.attempt(MongoZioDatabase(wrapped.getDatabase(name)))

  /**
    * Close the client, which will close all underlying cached resources, including, for example, sockets and background
    * monitoring threads.
    */
  def close(): Unit = wrapped.close()

  /**
    * Close the client , which will close all underlying cached resources, including, for example, sockets and
    * background monitoring threads.
    */
  def pureClose(): Task[Unit] = ZIO.attempt(close())

  /**
    * Get a list of the database names
    */
  def listDatabaseNames() = wrapped.listDatabaseNames().toZIOStream

  /**
    * Get a list of the database names
    */
  def listDatabaseNames(clientSession: ClientSession) =
    wrapped.listDatabaseNames(clientSession).toZIOStream()

  /**
    * Get a list of the database names
    */
  def listDatabases[T]()(implicit e: T MapTo Document, ct: ClassTag[T]) =
    wrapped.listDatabases(clazz(ct)).toZIOStream()

  /**
    * Gets the list of databases
    */
  def listDatabases[T](clientSession: ClientSession)(implicit e: T MapTo Document, ct: ClassTag[T]) =
    wrapped.listDatabases(clientSession, clazz(ct)).toZIOStream()

  /**
    * Creates a change stream for this client.
    */
  def watch[T]()(implicit e: T MapTo Document, ct: ClassTag[T]) =
    wrapped.watch(clazz(ct)).toZIOStream()

  /**
    * Creates a change stream for this collection.
    */
  def watch[T](pipeline: Seq[Bson])(implicit e: T MapTo Document, ct: ClassTag[T]) =
    wrapped.watch(pipeline.asJava, clazz(ct)).toZIOStream()

  /**
    * Creates a change stream for this collection.
    */
  def watch[T](clientSession: ClientSession)(implicit e: T MapTo Document, ct: ClassTag[T]) =
    wrapped.watch(clientSession, clazz(ct)).toZIOStream()

  /**
    * Creates a change stream for this collection.
    */
  def watch[T](clientSession: ClientSession, pipeline: Seq[Bson])(implicit e: T MapTo Document, ct: ClassTag[T]) =
    wrapped.watch(clientSession, pipeline.asJava, clazz(ct)).toZIOStream()

}

object MongoZioClient {

  /**
    * Create a default MongoZioClient at localhost:27017
    */
  def apply(): Task[MongoZioClient] = apply("mongodb://localhost:27017")

  /**
    * Create a MongoZioClient instance from a connection string uri
    */
  def apply(uri: String): Task[MongoZioClient] = MongoZioClient(uri, None)

  /**
    * Create a MongoZioClient instance from a connection string uri
    */
  def apply(uri: String, mongoDriverInformation: Option[MongoDriverInformation]): Task[MongoZioClient] =
    apply(
      MongoClientSettings
        .builder()
        .applyConnectionString(new ConnectionString(uri))
        .codecRegistry(DEFAULT_CODEC_REGISTRY)
        .build(),
      mongoDriverInformation
    )

  /**
    * Create a MongoZioClient instance from the MongoClientSettings
    */
  def apply(clientSettings: MongoClientSettings): Task[MongoZioClient] = MongoZioClient(clientSettings, None)

  /**
    * Create a MongoZioClient instance from the MongoClientSettings
    */
  def apply(
      clientSettings: MongoClientSettings,
      mongoDriverInformation: Option[MongoDriverInformation]
  ): Task[MongoZioClient] =
    ZIO.attempt(createMongoClient(clientSettings, mongoDriverInformation))

  private[bilalfazlani] def createMongoClient(
      clientSettings: MongoClientSettings,
      mongoDriverInformation: Option[MongoDriverInformation]
  ) = {
    val builder = mongoDriverInformation match {
      case Some(info) => MongoDriverInformation.builder(info)
      case None       => MongoDriverInformation.builder()
    }
    MongoZioClient(MongoClients.create(clientSettings, builder.build()))
  }

  val DEFAULT_CODEC_REGISTRY: CodecRegistry = fromRegistries(MongoClients.getDefaultCodecRegistry)
}
