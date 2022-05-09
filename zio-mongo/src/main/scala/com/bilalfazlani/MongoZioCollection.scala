package com.bilalfazlani.zioMongo

import com.bilalfazlani.zioMongo.result.Completed
import com.bilalfazlani.zioMongo.result.DeleteResult
import com.bilalfazlani.zioMongo.result.InsertManyResult
import com.bilalfazlani.zioMongo.result.InsertOneResult
import com.bilalfazlani.zioMongo.result.UpdateResult
import com.mongodb.MongoNamespace
import com.mongodb.ReadConcern
import com.mongodb.ReadPreference
import com.mongodb.WriteConcern
import com.mongodb.bulk.BulkWriteResult
import com.mongodb.client
import com.mongodb.client.model.*
import com.mongodb.reactivestreams.client.ClientSession
import org.bson
import org.bson.codecs.configuration.CodecRegistry
import org.bson.conversions.Bson
import org.mongodb.scala.bson.collection.immutable.Document
import org.reactivestreams.Publisher
import org.reactivestreams.Subscriber
import zio.IO
import zio.Task
import zio.ZIO
import zio.interop.reactivestreams.*
import zio.stream.ZSink
import zio.stream.ZStream

import java.{ util => ju }
import scala.jdk.CollectionConverters._
import scala.reflect.ClassTag

import result.*

case class MongoZioCollection[T](private val wrapped: JavaMongoCollection[T]) {

  /**
    * Gets the namespace of this collection.
    */
  lazy val namespace: MongoNamespace = wrapped.getNamespace

  /**
    * Get the default class to cast any documents returned from the database into.
    */
  lazy val documentClass: Class[T] = wrapped.getDocumentClass

  /**
    * Get the codec registry for the MongoDatabase.
    */
  lazy val codecRegistry: CodecRegistry = wrapped.getCodecRegistry

  /**
    * Get the read preference for the MongoDatabase.
    */
  lazy val readPreference: ReadPreference = wrapped.getReadPreference

  /**
    * Get the write concern for the MongoDatabase.
    */
  lazy val writeConcern: WriteConcern = wrapped.getWriteConcern

  /**
    * Get the read concern for the MongoDatabase.
    */
  lazy val readConcern: ReadConcern = wrapped.getReadConcern

  /**
    * Create a new MongoZioCollection instance with a different default class to cast any documents returned from the
    * database into..
    */
  def withDocumentClass()(using ct: ClassTag[T]): MongoZioCollection[T] =
    MongoZioCollection(wrapped.withDocumentClass(clazz(ct)))

  /**
    * Create a new MongoZioCollection instance with a different codec registry.
    */
  def withCodecRegistry(codecRegistry: CodecRegistry): MongoZioCollection[T] =
    MongoZioCollection(wrapped.withCodecRegistry(codecRegistry))

  /**
    * Create a new MongoZioCollection instance with a different read preference.
    */
  def withReadPreference(readPreference: ReadPreference): MongoZioCollection[T] =
    MongoZioCollection(wrapped.withReadPreference(readPreference))

  /**
    * Create a new MongoZioCollection instance with a different write concern.
    */
  def withWriteConcern(writeConcern: WriteConcern): MongoZioCollection[T] =
    MongoZioCollection(wrapped.withWriteConcern(writeConcern))

  /**
    * Create a new MongoZioCollection instance with a different read concern.
    */
  def withReadConcern(readConcern: ReadConcern): MongoZioCollection[T] =
    MongoZioCollection(wrapped.withReadConcern(readConcern))

  /**
    * Gets an estimate of the count of documents in a collection using collection metadata.
    */
  def estimatedDocumentCount() =
    wrapped.estimatedDocumentCount().toZIO

  /**
    * Gets an estimate of the count of documents in a collection using collection metadata.
    */
  def estimatedDocumentCount(options: EstimatedDocumentCountOptions) =
    wrapped.estimatedDocumentCount(options).toZIO

  /**
    * Counts the number of documents in the collection.
    */
  def countDocuments() =
    wrapped.countDocuments.toZIO

  /**
    * Counts the number of documents in the collection according to the given options.
    */
  def countDocuments(filter: Bson) =
    wrapped.countDocuments(filter).toZIO

  /**
    * Counts the number of documents in the collection according to the given options.
    */
  def countDocuments(filter: Bson, options: CountOptions) =
    wrapped.countDocuments(filter, options).toZIO

  /**
    * Counts the number of documents in the collection.
    */
  def countDocuments(clientSession: ClientSession) =
    wrapped.countDocuments(clientSession).toZIO

  /**
    * Counts the number of documents in the collection according to the given options.
    */
  def countDocuments(clientSession: ClientSession, filter: Bson) =
    wrapped.countDocuments(clientSession, filter).toZIO

  /**
    * Counts the number of documents in the collection according to the given options.
    */
  def countDocuments(clientSession: ClientSession, filter: Bson, options: CountOptions) =
    wrapped.countDocuments(clientSession, filter, options).toZIO

  /**
    * Gets the distinct values of the specified field name.
    */
  def distinct(fieldName: String)(using ct: ClassTag[T]) =
    wrapped.distinct(fieldName, clazz(ct)).toZIOStream()

  /**
    * Gets the distinct values of the specified field name.
    */
  def distinct(fieldName: String, filter: Bson)(using ct: ClassTag[T]) =
    wrapped.distinct(fieldName, filter, clazz(ct)).toZIOStream()

  /**
    * Gets the distinct values of the specified field name.
    */
  def distinct(clientSession: ClientSession, fieldName: String)(using ct: ClassTag[T]) =
    wrapped.distinct(clientSession, fieldName, clazz(ct)).toZIOStream()

  /**
    * Gets the distinct values of the specified field name.
    */
  def distinct(clientSession: ClientSession, fieldName: String, filter: Bson)(using
      ct: ClassTag[T]
  ) =
    wrapped.distinct(clientSession, fieldName, filter, clazz(ct)).toZIOStream()

  /**
    * Finds all documents in the collection.
    */
  def find(using ct: ClassTag[T]): ZStream[Any, Throwable, T] =
    wrapped.find(clazz(ct)).toZIOStream()

  /**
    * Finds all documents in the collection.
    */
  def find(filter: Bson)(using ct: ClassTag[T]): ZStream[Any, Throwable, T] =
    wrapped.find(filter, clazz(ct)).toZIOStream()

  /**
    * Finds all documents in the collection.
    */
  def find(clientSession: ClientSession)(using ct: ClassTag[T]): ZStream[Any, Throwable, T] =
    wrapped.find[T](clientSession, clazz(ct)).toZIOStream()

  /**
    * Finds all documents in the collection.
    */
  def find(clientSession: ClientSession, filter: Bson)(using
      ct: ClassTag[T]
  ): ZStream[Any, Throwable, T] =
    wrapped.find(clientSession, filter, clazz(ct)).toZIOStream()

  /**
    * Aggregates documents according to the specified aggregation pipeline.
    */
  def aggregate(pipeline: Seq[Bson])(using ct: ClassTag[T]) =
    wrapped.aggregate(pipeline.asJava, clazz(ct)).toZIOStream()

  /**
    * Aggregates documents according to the specified aggregation pipeline.
    */
  def aggregate(clientSession: ClientSession, pipeline: Seq[Bson])(using
      ct: ClassTag[T]
  ) =
    wrapped.aggregate(clientSession, pipeline.asJava, clazz(ct)).toZIOStream()

  /**
    * Aggregates documents according to the specified map-reduce function.
    */
  def mapReduce(mapFunction: String, reduceFunction: String)(using
      ct: ClassTag[T]
  ) =
    wrapped.mapReduce(mapFunction, reduceFunction, clazz(ct)).toZIOStream()

  /**
    * Aggregates documents according to the specified map-reduce function.
    */
  def mapReduce(clientSession: ClientSession, mapFunction: String, reduceFunction: String)(using
      ct: ClassTag[T]
  ) =
    wrapped.mapReduce(clientSession, mapFunction, reduceFunction, clazz(ct)).toZIOStream()

  /**
    * Executes a mix of inserts, updates, replaces, and deletes.
    */
  def bulkWrite(requests: Seq[_ <: WriteModel[_ <: T]]): Task[BulkWriteResult] =
    wrapped.bulkWrite(requests.asJava).toZIO

  /**
    * Executes a mix of inserts, updates, replaces, and deletes.
    */
  def bulkWrite(requests: Seq[_ <: WriteModel[_ <: T]], options: BulkWriteOptions): Task[BulkWriteResult] =
    wrapped.bulkWrite(requests.asJava, options).toZIO

  /**
    * Executes a mix of inserts, updates, replaces, and deletes.
    */
  def bulkWrite(
      clientSession: ClientSession,
      requests: Seq[_ <: WriteModel[_ <: T]]
  ): Task[BulkWriteResult] =
    wrapped.bulkWrite(clientSession, requests.asJava).toZIO

  /**
    * Executes a mix of inserts, updates, replaces, and deletes.
    */
  def bulkWrite(
      clientSession: ClientSession,
      requests: Seq[_ <: WriteModel[_ <: T]],
      options: BulkWriteOptions
  ): Task[BulkWriteResult] =
    wrapped.bulkWrite(clientSession, requests.asJava, options).toZIO

  /**
    * Inserts the provided document. If the document is missing an identifier, the driver should generate one.
    */
  def insertOne(document: T): Task[InsertOneResult] =
    wrapped.insertOne(document).toZIO.map(InsertOneResult.apply)

  /**
    * Inserts the provided document. If the document is missing an identifier, the driver should generate one.
    */
  def insertOne(document: T, options: InsertOneOptions): Task[InsertOneResult] =
    wrapped.insertOne(document, options).toZIO.map(InsertOneResult.apply)

  /**
    * Inserts the provided document. If the document is missing an identifier, the driver should generate one.
    */
  def insertOne(clientSession: ClientSession, document: T): Task[InsertOneResult] =
    wrapped.insertOne(clientSession, document).toZIO.map(InsertOneResult.apply)

  /**
    * Inserts the provided document. If the document is missing an identifier, the driver should generate one.
    */
  def insertOne(clientSession: ClientSession, document: T, options: InsertOneOptions): Task[InsertOneResult] =
    wrapped.insertOne(clientSession, document, options).toZIO.map(InsertOneResult.apply)

  /**
    * Inserts a batch of documents. The preferred way to perform bulk inserts is to use the BulkWrite API. However, when
    * talking with a server &lt; 2.6, using this method will be faster due to constraints in the bulk API related to
    * error handling.
    */
  def insertMany(documents: Seq[_ <: T]): Task[InsertManyResult] =
    wrapped.insertMany(documents.asJava).toZIO.map(InsertManyResult.apply)

  /**
    * Inserts a batch of documents. The preferred way to perform bulk inserts is to use the BulkWrite API. However, when
    * talking with a server &lt; 2.6, using this method will be faster due to constraints in the bulk API related to
    * error handling.
    */
  def insertMany(documents: Seq[_ <: T], options: InsertManyOptions): Task[InsertManyResult] =
    wrapped.insertMany(documents.asJava, options).toZIO.map(InsertManyResult.apply)

  /**
    * Inserts a batch of documents. The preferred way to perform bulk inserts is to use the BulkWrite API.
    */
  def insertMany(clientSession: ClientSession, documents: Seq[_ <: T]): Task[InsertManyResult] =
    wrapped.insertMany(clientSession, documents.asJava).toZIO.map(InsertManyResult.apply)

  /**
    * Inserts a batch of documents. The preferred way to perform bulk inserts is to use the BulkWrite API.
    */
  def insertMany(
      clientSession: ClientSession,
      documents: Seq[_ <: T],
      options: InsertManyOptions
  ): Task[InsertManyResult] =
    wrapped.insertMany(clientSession, documents.asJava, options).toZIO.map(InsertManyResult.apply)

  /**
    * Removes at most one document from the collection that matches the given filter. If no documents match, the
    * collection is not modified.
    */
  def deleteOne(filter: Bson): Task[DeleteResult] =
    wrapped.deleteOne(filter).toZIO.map(DeleteResult.apply)

  /**
    * Removes at most one document from the collection that matches the given filter. If no documents match, the
    * collection is not modified.
    */
  def deleteOne(filter: Bson, options: DeleteOptions): Task[DeleteResult] =
    wrapped.deleteOne(filter, options).toZIO.map(DeleteResult.apply)

  /**
    * Removes at most one document from the collection that matches the given filter. If no documents match, the
    * collection is not modified.
    */
  def deleteOne(clientSession: ClientSession, filter: Bson): Task[DeleteResult] =
    wrapped.deleteOne(clientSession, filter).toZIO.map(DeleteResult.apply)

  /**
    * Removes at most one document from the collection that matches the given filter. If no documents match, the
    * collection is not modified.
    */
  def deleteOne(clientSession: ClientSession, filter: Bson, options: DeleteOptions): Task[DeleteResult] =
    wrapped.deleteOne(clientSession, filter, options).toZIO.map(DeleteResult.apply)

  /**
    * Removes all documents from the collection that match the given query filter. If no documents match, the collection
    * is not modified.
    */
  def deleteMany(filter: Bson): Task[DeleteResult] =
    wrapped.deleteMany(filter).toZIO.map(DeleteResult.apply)

  /**
    * Removes all documents from the collection that match the given query filter. If no documents match, the collection
    * is not modified.
    */
  def deleteMany(filter: Bson, options: DeleteOptions): Task[DeleteResult] =
    wrapped.deleteMany(filter, options).toZIO.map(DeleteResult.apply)

  /**
    * Removes all documents from the collection that match the given query filter. If no documents match, the collection
    * is not modified.
    */
  def deleteMany(clientSession: ClientSession, filter: Bson): Task[DeleteResult] =
    wrapped.deleteMany(clientSession, filter).toZIO.map(DeleteResult.apply)

  /**
    * Removes all documents from the collection that match the given query filter. If no documents match, the collection
    * is not modified.
    */
  def deleteMany(clientSession: ClientSession, filter: Bson, options: DeleteOptions): Task[DeleteResult] =
    wrapped.deleteMany(clientSession, filter, options).toZIO.map(DeleteResult.apply)

  /**
    * Replace a document in the collection according to the specified arguments.
    */
  def replaceOne(filter: Bson, replacement: T): Task[client.result.UpdateResult] =
    wrapped.replaceOne(filter, replacement).toZIO

  /**
    * Replace a document in the collection according to the specified arguments.
    */
  def replaceOne(clientSession: ClientSession, filter: Bson, replacement: T): Task[UpdateResult] =
    wrapped.replaceOne(clientSession, filter, replacement).toZIO.map(UpdateResult.apply)

  /**
    * Replace a document in the collection according to the specified arguments.
    */
  def replaceOne(filter: Bson, replacement: T, options: ReplaceOptions): Task[UpdateResult] =
    wrapped.replaceOne(filter, replacement, options).toZIO.map(UpdateResult.apply)

  /**
    * Replace a document in the collection according to the specified arguments.
    */
  def replaceOne(
      clientSession: ClientSession,
      filter: Bson,
      replacement: T,
      options: ReplaceOptions
  ): Task[UpdateResult] =
    wrapped.replaceOne(clientSession, filter, replacement, options).toZIO.map(UpdateResult.apply)

  /**
    * Update a single document in the collection according to the specified arguments.
    */
  def updateOne(filter: Bson, update: Bson): Task[UpdateResult] =
    wrapped.updateOne(filter, update).toZIO.map(UpdateResult.apply)

  /**
    * Update a single document in the collection according to the specified arguments.
    */
  def updateOne(filter: Bson, update: Bson, options: UpdateOptions): Task[UpdateResult] =
    wrapped.updateOne(filter, update, options).toZIO.map(UpdateResult.apply)

  /**
    * Update a single document in the collection according to the specified arguments.
    */
  def updateOne(clientSession: ClientSession, filter: Bson, update: Bson): Task[UpdateResult] =
    wrapped.updateOne(clientSession, filter, update).toZIO.map(UpdateResult.apply)

  /**
    * Update a single document in the collection according to the specified arguments.
    */
  def updateOne(
      clientSession: ClientSession,
      filter: Bson,
      update: Bson,
      options: UpdateOptions
  ): Task[UpdateResult] =
    wrapped.updateOne(clientSession, filter, update, options).toZIO.map(UpdateResult.apply)

  /**
    * Update a single document in the collection according to the specified arguments.
    */
  def updateOne(filter: Bson, update: Seq[Bson]): Task[UpdateResult] =
    wrapped.updateOne(filter, update.asJava).toZIO.map(UpdateResult.apply)

  /**
    * Update a single document in the collection according to the specified arguments.
    */
  def updateOne(filter: Bson, update: Seq[Bson], options: UpdateOptions): Task[UpdateResult] =
    wrapped.updateOne(filter, update.asJava, options).toZIO.map(UpdateResult.apply)

  /**
    * Update a single document in the collection according to the specified arguments.
    */
  def updateOne(clientSession: ClientSession, filter: Bson, update: Seq[Bson]): Task[UpdateResult] =
    wrapped.updateOne(clientSession, filter, update.asJava).toZIO.map(UpdateResult.apply)

  /**
    * Update a single document in the collection according to the specified arguments.
    */
  def updateOne(
      clientSession: ClientSession,
      filter: Bson,
      update: Seq[Bson],
      options: UpdateOptions
  ): Task[UpdateResult] =
    wrapped.updateOne(clientSession, filter, update.asJava, options).toZIO.map(UpdateResult.apply)

  /**
    * Update a single document in the collection according to the specified arguments.
    */
  def updateMany(filter: Bson, update: Bson): Task[UpdateResult] =
    wrapped.updateMany(filter, update).toZIO.map(UpdateResult.apply)

  /**
    * Update a single document in the collection according to the specified arguments.
    */
  def updateMany(filter: Bson, update: Bson, options: UpdateOptions): Task[UpdateResult] =
    wrapped.updateMany(filter, update, options).toZIO.map(UpdateResult.apply)

  /**
    * Update a single document in the collection according to the specified arguments.
    */
  def updateMany(clientSession: ClientSession, filter: Bson, update: Bson): Task[UpdateResult] =
    wrapped.updateMany(clientSession, filter, update).toZIO.map(UpdateResult.apply)

  /**
    * Update a single document in the collection according to the specified arguments.
    */
  def updateMany(
      clientSession: ClientSession,
      filter: Bson,
      update: Bson,
      options: UpdateOptions
  ): Task[UpdateResult] =
    wrapped.updateMany(clientSession, filter, update, options).toZIO.map(UpdateResult.apply)

  /**
    * Update a single document in the collection according to the specified arguments.
    */
  def updateMany(filter: Bson, update: Seq[Bson]): Task[UpdateResult] =
    wrapped.updateMany(filter, update.asJava).toZIO.map(UpdateResult.apply)

  /**
    * Update a single document in the collection according to the specified arguments.
    */
  def updateMany(filter: Bson, update: Seq[Bson], options: UpdateOptions): Task[UpdateResult] =
    wrapped.updateMany(filter, update.asJava, options).toZIO.map(UpdateResult.apply)

  /**
    * Update a single document in the collection according to the specified arguments.
    */
  def updateMany(clientSession: ClientSession, filter: Bson, update: Seq[Bson]): Task[UpdateResult] =
    wrapped.updateMany(clientSession, filter, update.asJava).toZIO.map(UpdateResult.apply)

  /**
    * Update a single document in the collection according to the specified arguments.
    */
  def updateMany(
      clientSession: ClientSession,
      filter: Bson,
      update: Seq[Bson],
      options: UpdateOptions
  ): Task[UpdateResult] =
    wrapped.updateMany(clientSession, filter, update.asJava, options).toZIO.map(UpdateResult.apply)

  /**
    * Atomically find a document and remove it.
    */
  def findOneAndDelete(filter: Bson): Task[T] =
    wrapped.findOneAndDelete(filter).toZIO

  /**
    * Atomically find a document and remove it.
    */
  def findOneAndDelete(filter: Bson, options: FindOneAndDeleteOptions): Task[T] =
    wrapped.findOneAndDelete(filter, options).toZIO

  /**
    * Atomically find a document and remove it.
    */
  def findOneAndDelete(clientSession: ClientSession, filter: Bson): Task[T] =
    wrapped.findOneAndDelete(clientSession, filter).toZIO

  /**
    * Atomically find a document and remove it.
    */
  def findOneAndDelete(
      clientSession: ClientSession,
      filter: Bson,
      options: FindOneAndDeleteOptions
  ): Task[T] =
    wrapped.findOneAndDelete(clientSession, filter, options).toZIO

  /**
    * Atomically find a document and replace it.
    */
  def findOneAndReplace(filter: Bson, replacement: T): Task[T] =
    wrapped.findOneAndReplace(filter, replacement).toZIO

  /**
    * Atomically find a document and replace it.
    */
  def findOneAndReplace(filter: Bson, replacement: T, options: FindOneAndReplaceOptions): Task[T] =
    wrapped.findOneAndReplace(filter, replacement, options).toZIO

  /**
    * Atomically find a document and replace it.
    */
  def findOneAndReplace(clientSession: ClientSession, filter: Bson, replacement: T): Task[T] =
    wrapped.findOneAndReplace(clientSession, filter, replacement).toZIO

  /**
    * Atomically find a document and replace it.
    */
  def findOneAndReplace(
      clientSession: ClientSession,
      filter: Bson,
      replacement: T,
      options: FindOneAndReplaceOptions
  ): Task[T] =
    wrapped.findOneAndReplace(clientSession, filter, replacement, options).toZIO

  /**
    * Atomically find a document and update it.
    */
  def findOneAndUpdate(filter: Bson, update: Bson): Task[T] =
    wrapped.findOneAndUpdate(filter, update).toZIO

  /**
    * Atomically find a document and update it.
    */
  def findOneAndUpdate(filter: Bson, update: Bson, options: FindOneAndUpdateOptions): Task[T] =
    wrapped.findOneAndUpdate(filter, update, options).toZIO

  /**
    * Atomically find a document and update it.
    */
  def findOneAndUpdate(clientSession: ClientSession, filter: Bson, update: Bson): Task[T] =
    wrapped.findOneAndUpdate(clientSession, filter, update).toZIO

  /**
    * Atomically find a document and update it.
    */
  def findOneAndUpdate(
      clientSession: ClientSession,
      filter: Bson,
      update: Bson,
      options: FindOneAndUpdateOptions
  ): Task[T] =
    wrapped.findOneAndUpdate(clientSession, filter, update, options).toZIO

  /**
    * Atomically find a document and update it.
    */
  def findOneAndUpdate(filter: Bson, update: Seq[Bson]): Task[T] =
    wrapped.findOneAndUpdate(filter, update.asJava).toZIO

  /**
    * Atomically find a document and update it.
    */
  def findOneAndUpdate(filter: Bson, update: Seq[Bson], options: FindOneAndUpdateOptions): Task[T] =
    wrapped.findOneAndUpdate(filter, update.asJava, options).toZIO

  /**
    * Atomically find a document and update it.
    */
  def findOneAndUpdate(clientSession: ClientSession, filter: Bson, update: Seq[Bson]): Task[T] =
    wrapped.findOneAndUpdate(clientSession, filter, update.asJava).toZIO

  /**
    * Atomically find a document and update it.
    */
  def findOneAndUpdate(
      clientSession: ClientSession,
      filter: Bson,
      update: Seq[Bson],
      options: FindOneAndUpdateOptions
  ): Task[T] =
    wrapped.findOneAndUpdate(clientSession, filter, update.asJava, options).toZIO

  /**
    * Drops this collection from the Database.
    */
  def drop(): Task[Completed] = wrapped.drop().toCompleted

  /**
    * Drops this collection from the Database.
    */
  def drop(clientSession: ClientSession): Task[Completed] = wrapped.drop(clientSession).toCompleted

  /**
    * Creates an index
    */
  def createIndex(key: Bson): Task[String] =
    wrapped.createIndex(key).toZIO

  /**
    * Creates an index
    */
  def createIndex(key: Bson, options: IndexOptions): Task[String] =
    wrapped.createIndex(key, options).toZIO

  /**
    * Creates an index
    */
  def createIndex(clientSession: ClientSession, key: Bson): Task[String] =
    wrapped.createIndex(clientSession, key).toZIO

  /**
    * Creates an index
    */
  def createIndex(clientSession: ClientSession, key: Bson, options: IndexOptions): Task[String] =
    wrapped.createIndex(clientSession, key, options).toZIO

  /**
    * Creates an index
    */
  def createIndexes(models: Seq[IndexModel]): Task[String] =
    wrapped.createIndexes(models.asJava).toZIO

  /**
    * Create multiple indexes.
    */
  def createIndexes(models: Seq[IndexModel], createIndexOptions: CreateIndexOptions): Task[String] =
    wrapped.createIndexes(models.asJava, createIndexOptions).toZIO

  /**
    * Create multiple indexes.
    */
  def createIndexes(clientSession: ClientSession, models: Seq[IndexModel]): Task[String] =
    wrapped.createIndexes(clientSession, models.asJava).toZIO

  /**
    * Create multiple indexes.
    */
  def createIndexes(
      clientSession: ClientSession,
      models: Seq[IndexModel],
      createIndexOptions: CreateIndexOptions
  ): Task[String] =
    wrapped.createIndexes(clientSession, models.asJava, createIndexOptions).toZIO

  /**
    * Get all the indexes in this collection.
    */
  def listIndexes()(using ct: ClassTag[T]) =
    wrapped.listIndexes(clazz(ct)).toZIOStream()

  /**
    * Get all the indexes in this collection.
    */
  def listIndexes(
      clientSession: ClientSession
  )(using ct: ClassTag[T]) =
    wrapped.listIndexes(clientSession, clazz(ct)).toZIOStream()

  /**
    * Drops the given index.
    */
  def dropIndex(indexName: String): Task[Completed] =
    wrapped.dropIndex(indexName).toCompleted

  /**
    * Drops the given index.
    */
  def dropIndex(indexName: String, dropIndexOptions: DropIndexOptions): Task[Completed] =
    wrapped.dropIndex(indexName, dropIndexOptions).toCompleted

  /**
    * Drops the index given the keys used to create it.
    */
  def dropIndex(keys: Bson): Task[Completed] =
    wrapped.dropIndex(keys).toCompleted

  /**
    * Drops the index given the keys used to create it.
    */
  def dropIndex(keys: Bson, dropIndexOptions: DropIndexOptions): Task[Completed] =
    wrapped.dropIndex(keys, dropIndexOptions).toCompleted

  /**
    * Drops the given index.
    */
  def dropIndex(clientSession: ClientSession, indexName: String) =
    wrapped.dropIndex(clientSession, indexName).toZIO

  /**
    * Drops the given index.
    */
  def dropIndex(
      clientSession: ClientSession,
      indexName: String,
      dropIndexOptions: DropIndexOptions
  ): Task[Completed] =
    wrapped.dropIndex(clientSession, indexName, dropIndexOptions).toCompleted

  /**
    * Drops the index given the keys used to create it.
    */
  def dropIndex(clientSession: ClientSession, keys: Bson): Task[Completed] =
    wrapped.dropIndex(clientSession, keys).toCompleted

  /**
    * Drops the index given the keys used to create it.
    */
  def dropIndex(
      clientSession: ClientSession,
      keys: Bson,
      dropIndexOptions: DropIndexOptions
  ): Task[Completed] =
    wrapped.dropIndex(clientSession, keys, dropIndexOptions).toCompleted

  /**
    * Drop all the indexes on this collection, except for the default on _id.
    */
  def dropIndexes(): Task[Completed] =
    wrapped.dropIndexes().toCompleted

  /**
    * Drop all the indexes on this collection, except for the default on _id.
    */
  def dropIndexes(dropIndexOptions: DropIndexOptions): Task[Completed] =
    wrapped.dropIndexes(dropIndexOptions).toCompleted

  /**
    * Drop all the indexes on this collection, except for the default on _id.
    */
  def dropIndexes(clientSession: ClientSession): Task[Completed] =
    wrapped.dropIndexes(clientSession).toCompleted

  /**
    * Drop all the indexes on this collection, except for the default on _id.
    */
  def dropIndexes(clientSession: ClientSession, dropIndexOptions: DropIndexOptions): Task[Completed] =
    wrapped.dropIndexes(clientSession, dropIndexOptions).toCompleted

  /**
    * Rename the collection with oldCollectionName to the newCollectionName.
    */
  def renameCollection(newCollectionNamespace: MongoNamespace): Task[Completed] =
    wrapped.renameCollection(newCollectionNamespace).toCompleted

  /**
    * Rename the collection with oldCollectionName to the newCollectionName.
    */
  def renameCollection(
      newCollectionNamespace: MongoNamespace,
      options: RenameCollectionOptions
  ): Task[Completed] =
    wrapped.renameCollection(newCollectionNamespace, options).toCompleted

  /**
    * Rename the collection with oldCollectionName to the newCollectionName.
    */
  def renameCollection(clientSession: ClientSession, newCollectionNamespace: MongoNamespace): Task[Completed] =
    wrapped.renameCollection(clientSession, newCollectionNamespace).toCompleted

  /**
    * Rename the collection with oldCollectionName to the newCollectionName.
    *
    * [[http://docs.mongodb.org/manual/reference/commands/renameCollection Rename collection]]
    * @param clientSession
    *   the client session with which to associate this operation
    * @param newCollectionNamespace
    *   the name the collection will be renamed to
    * @param options
    *   the options for renaming a collection
    * @return
    *   an IO with a single element indicating when the operation has completed
    * @since 2.2
    * @note
    *   Requires MongoDB 3.6 or greater
    */
  def renameCollection(
      clientSession: ClientSession,
      newCollectionNamespace: MongoNamespace,
      options: RenameCollectionOptions
  ): Task[Completed] =
    wrapped.renameCollection(clientSession, newCollectionNamespace, options).toCompleted

  /**
    * Creates a change stream for this collection.
    *
    * @tparam C
    *   the target document type of the observable.
    * @return
    *   the change stream observable
    * @since 2.2
    * @note
    *   Requires MongoDB 3.6 or greater
    */
  def watch() = wrapped.watch().toZIOStream()

  /**
    * Creates a change stream for this collection.
    *
    * @param pipeline
    *   the aggregation pipeline to apply to the change stream
    * @tparam C
    *   the target document type of the observable.
    * @return
    *   the change stream observable
    * @since 2.2
    * @note
    *   Requires MongoDB 3.6 or greater
    */
  def watch(pipeline: Seq[Bson]) = wrapped.watch(pipeline.asJava).toZIOStream()

  /**
    * Creates a change stream for this collection.
    *
    * @param clientSession
    *   the client session with which to associate this operation
    * @tparam C
    *   the target document type of the observable.
    * @return
    *   the change stream observable
    * @since 2.2
    * @note
    *   Requires MongoDB 3.6 or greater
    */
  def watch(clientSession: ClientSession) = wrapped.watch(clientSession).toZIOStream()

  /**
    * Creates a change stream for this collection.
    *
    * @param clientSession
    *   the client session with which to associate this operation
    * @param pipeline
    *   the aggregation pipeline to apply to the change stream
    * @tparam C
    *   the target document type of the observable.
    * @return
    *   the change stream observable
    * @since 2.2
    * @note
    *   Requires MongoDB 3.6 or greater
    */
  def watch(clientSession: ClientSession, pipeline: Seq[Bson]) =
    wrapped.watch(clientSession, pipeline.asJava).toZIOStream()

}
