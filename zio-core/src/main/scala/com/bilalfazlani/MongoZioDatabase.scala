package com.bilalfazlani

import com.mongodb.client.model.changestream.ChangeStreamDocument
import com.mongodb.client.model.{CreateCollectionOptions, CreateViewOptions}
import com.mongodb.{ReadConcern, ReadPreference, WriteConcern}
import com.mongodb.reactivestreams.client.ClientSession
import com.bilalfazlani.DefaultHelper.MapTo
import com.bilalfazlani.result.Completed
import org.bson
import zio.interop.reactivestreams.*
import scala.jdk.CollectionConverters.*
import org.bson.codecs.configuration.CodecRegistry
import org.bson.conversions.Bson
import org.mongodb.scala.bson.collection.immutable.Document
import zio.{IO, Task, ZIO}

import scala.reflect.ClassTag

case class MongoZioDatabase(private val javaMongoDatabase: JavaMongoDatabase) {

  /**
    * Gets the name of the database.
    *
    */
  lazy val name: String = javaMongoDatabase.getName

  /**
    * Get the codec registry for the MongoDatabase.
    *
    */
  lazy val codecRegistry: CodecRegistry = javaMongoDatabase.getCodecRegistry

  /**
    * Get the read preference for the MongoDatabase.
    *
    */
  lazy val readPreference: ReadPreference = javaMongoDatabase.getReadPreference

  /**
    * Get the write concern for the MongoDatabase.
    *
    */
  lazy val writeConcern: WriteConcern = javaMongoDatabase.getWriteConcern

  /**
    * Get the read concern for the MongoDatabase.
    *
    */
  lazy val readConcern: ReadConcern = javaMongoDatabase.getReadConcern

  /**
    * Create a new MongoZioDatabase instance with a different codec registry.
    *
    */
  def withCodecRegistry(codecRegistry: CodecRegistry): MongoZioDatabase =
    MongoZioDatabase(javaMongoDatabase.withCodecRegistry(codecRegistry))

  /**
    * Create a new MongoZioDatabase instance with a different read preference.
    *
    */
  def withReadPreference(readPreference: ReadPreference): MongoZioDatabase =
    MongoZioDatabase(javaMongoDatabase.withReadPreference(readPreference))

  /**
    * Create a new MongoZioDatabase instance with a different write concern.
    */
  def withWriteConcern(writeConcern: WriteConcern): MongoZioDatabase =
    MongoZioDatabase(javaMongoDatabase.withWriteConcern(writeConcern))

  /**
    * Create a new MongoZioDatabase instance with a different read concern.
    *
    */
  def withReadConcern(readConcern: ReadConcern): MongoZioDatabase =
    MongoZioDatabase(javaMongoDatabase.withReadConcern(readConcern))

  /**
    * Gets a MongoZioCollection, with a specific default document class.
    *
    */
  def getCollection[TResult](collectionName: String)(implicit e: TResult MapTo Document, ct: ClassTag[TResult]): Task[MongoZioCollection[TResult]] =
    ZIO.attempt(MongoZioCollection(javaMongoDatabase.getCollection(collectionName, clazz(ct))))

  /**
    * Executes command in the context of the current database.
    */
  def runCommand[TResult](command: Bson)(implicit e: TResult MapTo Document, ct: ClassTag[TResult]): Task[TResult] =
    javaMongoDatabase.runCommand[TResult](command, clazz(ct)).toZIO

  /**
    * Executes command in the context of the current database.
    */
  def runCommand[TResult](command: Bson, readPreference: ReadPreference)(implicit e: TResult MapTo Document, ct: ClassTag[TResult]): Task[TResult] =
    javaMongoDatabase.runCommand(command, readPreference, clazz(ct)).toZIO

  /**
    * Executes command in the context of the current database.
    */
  def runCommand[TResult](clientSession: ClientSession, command: Bson)(implicit e: TResult MapTo Document, ct: ClassTag[TResult]): Task[TResult] =
    javaMongoDatabase.runCommand[TResult](clientSession, command, clazz(ct)).toZIO

  /**
    * Executes command in the context of the current database.
    */
  def runCommand[TResult](clientSession: ClientSession, command: Bson, readPreference: ReadPreference)(implicit e: TResult MapTo Document, ct: ClassTag[TResult]): Task[TResult] =
    javaMongoDatabase.runCommand(clientSession, command, readPreference, clazz(ct)).toZIO

  /**
    * Drops this database.
    *
    */
  def drop(): Task[Completed] = javaMongoDatabase.drop().toCompleted

  /**
    * Drops this database.
    */
  def drop(clientSession: ClientSession): Task[Completed] =
    javaMongoDatabase.drop(clientSession).toCompleted

  /**
    * Gets the names of all the collections in this database.
    */
  def listCollectionNames(): Task[String] = javaMongoDatabase.listCollectionNames().toZIO

  /**
    * Finds all the collections in this database.
    *
    */
  def listCollections[TResult]()(implicit e: TResult MapTo Document, ct: ClassTag[TResult]) =
    javaMongoDatabase.listCollections(clazz(ct)).toZIOStream()

  /**
    * Gets the names of all the collections in this database.
    *
    */
  def listCollectionNames(clientSession: ClientSession): Task[String] = javaMongoDatabase.listCollectionNames(clientSession).toZIO

  /**
    * Finds all the collections in this database.
    *
    */
  def listCollections[TResult](clientSession: ClientSession)(implicit e: TResult MapTo Document, ct: ClassTag[TResult]) =
    javaMongoDatabase.listCollections(clientSession, clazz(ct)).toZIOStream()

  /**
    * Create a new collection with the given name.
    */
  def createCollection(collectionName: String): Task[Completed] =
    javaMongoDatabase.createCollection(collectionName).toCompleted

  /**
    * Create a new collection with the selected options
    *
    */
  def createCollection(collectionName: String, options: CreateCollectionOptions): Task[Completed] =
    javaMongoDatabase.createCollection(collectionName, options).toCompleted

  /**
    * Create a new collection with the given name.
    *
    */
  def createCollection(clientSession: ClientSession, collectionName: String): Task[Completed] =
    javaMongoDatabase.createCollection(clientSession, collectionName).toCompleted

  /**
    * Create a new collection with the selected options
    *
    */
  def createCollection(clientSession: ClientSession, collectionName: String, options: CreateCollectionOptions): Task[Completed] =
    javaMongoDatabase.createCollection(clientSession, collectionName, options).toCompleted

  /**
    * Creates a view with the given name, backing collection/view name, and aggregation pipeline that defines the view.
    */
  def createView(viewName: String, viewOn: String, pipeline: Seq[Bson]): Task[Completed] =
    javaMongoDatabase.createView(viewName, viewOn, pipeline.asJava).toCompleted

  /**
    * Creates a view with the given name, backing collection/view name, aggregation pipeline, and options that defines the view.
    */
  def createView(viewName: String, viewOn: String, pipeline: Seq[Bson], createViewOptions: CreateViewOptions): Task[Completed] =
    javaMongoDatabase.createView(viewName, viewOn, pipeline.asJava, createViewOptions).toCompleted

  /**
    * Creates a view with the given name, backing collection/view name, and aggregation pipeline that defines the view.
    */
  def createView(clientSession: ClientSession, viewName: String, viewOn: String, pipeline: Seq[Bson]): Task[Completed] =
    javaMongoDatabase.createView(clientSession, viewName, viewOn, pipeline.asJava).toCompleted

  /**
    * Creates a view with the given name, backing collection/view name, aggregation pipeline, and options that defines the view.
    *
    */
  def createView(clientSession: ClientSession, viewName: String, viewOn: String, pipeline: Seq[Bson], createViewOptions: CreateViewOptions): Task[Completed] =
    javaMongoDatabase.createView(clientSession, viewName, viewOn, pipeline.asJava, createViewOptions).toCompleted

  /**
    * Creates a change stream for this collection.
    */
  def watch() =
    javaMongoDatabase.watch().toZIOStream()

  /**
    * Creates a change stream for this collection.
    *
    */
  def watch(pipeline: Seq[Bson]) =
    javaMongoDatabase.watch(pipeline.asJava).toZIOStream()

  /**
    * Creates a change stream for this collection.
    *
    */
  def watch(clientSession: ClientSession) =
    javaMongoDatabase.watch(clientSession).toZIOStream()

  /**
    * Creates a change stream for this collection.
    *
    */
  def watch(clientSession: ClientSession, pipeline: Seq[Bson]) =
    javaMongoDatabase.watch(clientSession, pipeline.asJava).toZIOStream()

  /**
    * Aggregates documents according to the specified aggregation pipeline.
    *
    */
  def aggregate[C](pipeline: Seq[Bson])(ct: ClassTag[C]) =
    javaMongoDatabase.aggregate[C](pipeline.asJava,  ct.runtimeClass.asInstanceOf[Class[C]]).toZIOStream()

  /**
    * Aggregates documents according to the specified aggregation pipeline.
    *
    */
  def aggregate[C](clientSession: ClientSession, pipeline: Seq[Bson])(ct: ClassTag[C]) =
    javaMongoDatabase.aggregate(clientSession, pipeline.asJava, ct.runtimeClass.asInstanceOf[Class[C]]).toZIOStream()
}
