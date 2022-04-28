package com.bilalfazlani.subscriptions

import com.mongodb.client.model.Collation
import com.mongodb.reactivestreams.client.DistinctPublisher
import org.bson.conversions.Bson
import zio.IO
import zio.interop.reactivestreams.*
import java.util.concurrent.TimeUnit
import scala.collection.mutable.ArrayBuffer
import zio.stream.ZSink

case class DistinctSubscription[T](p: DistinctPublisher[T]) extends StreamSubscription[T] {

  override def fetch = p.toStream()

  def headOption: IO[Throwable, Option[T]] = fetch.run(ZSink.head)

  def filter(filter: Bson): DistinctSubscription[T] = this.copy(p.filter(filter))

  def maxTime(maxTime: Long, timeUnit: TimeUnit): DistinctSubscription[T] = this.copy(p.maxTime(maxTime, timeUnit))

  def collation(collation: Collation): DistinctSubscription[T] = this.copy(p.collation(collation))

  def batchSize(batchSize: Int): DistinctSubscription[T] = this.copy(p.batchSize(batchSize))

  def first(): SingleItemSubscription[T] = SingleItemSubscription(p.first())

}
