package com.bilalfazlani.subscriptions

import com.mongodb.client.model.Collation
import com.mongodb.{ CursorType, ExplainVerbosity }
import com.mongodb.reactivestreams.client.FindPublisher
import org.bson.Document
import org.bson.conversions.Bson
import zio.IO
import zio.interop.reactivestreams.*
import java.util.concurrent.TimeUnit
import scala.collection.mutable.ArrayBuffer
import zio.stream.ZStream

case class FindSubscription[T](p: FindPublisher[T]) extends StreamSubscription[T] {
  override def fetch: ZStream[Any, Throwable, T] =
    p.toStream()
}
