package com.bilalfazlani.subscriptions

import com.mongodb.reactivestreams.client.ListCollectionsPublisher
import org.bson.conversions.Bson
import org.reactivestreams.{Subscription => JSubscription}
import zio.IO
import zio.interop.reactivestreams.*
import java.util.concurrent.TimeUnit
import scala.collection.mutable.ArrayBuffer

case class ListCollectionsSubscription[T](p: ListCollectionsPublisher[T]) extends StreamSubscription[T] {

  override def fetch = p.toStream()

  def filter(filter: Bson): ListCollectionsSubscription[T] = this.copy(p.filter(filter))

  def maxTime(maxTime: Long, timeUnit: TimeUnit): ListCollectionsSubscription[T] =
    this.copy(p.maxTime(maxTime, timeUnit))

  def batchSize(batchSize: Int): ListCollectionsSubscription[T] = this.copy(p.batchSize(batchSize))

  def first(): SingleItemSubscription[T] = SingleItemSubscription(p.first())

}