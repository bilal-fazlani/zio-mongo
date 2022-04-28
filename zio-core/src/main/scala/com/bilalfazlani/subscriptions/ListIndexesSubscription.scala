package com.bilalfazlani.subscriptions

import com.mongodb.reactivestreams.client.ListIndexesPublisher
import zio.IO
import zio.interop.reactivestreams.*
import java.util.concurrent.TimeUnit
import scala.collection.mutable.ArrayBuffer

case class ListIndexesSubscription[T](p: ListIndexesPublisher[T]) extends StreamSubscription[T] {

  override def fetch = p.toStream()

  def maxTime(maxTime: Long, timeUnit: TimeUnit): ListIndexesSubscription[T] = this.copy(p.maxTime(maxTime, timeUnit))

  def batchSize(batchSize: Int): ListIndexesSubscription[T] = this.copy(p.batchSize(batchSize))

  def first(): SingleItemSubscription[T] = SingleItemSubscription(p.first())

}
