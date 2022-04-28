package com.bilalfazlani.subscriptions

import zio.IO
import zio.interop.reactivestreams.*
import scala.collection.mutable.ArrayBuffer

case class ListSubscription[T](p: JavaPublisher[T]) extends StreamSubscription[T] {
  override def fetch = p.toStream()
}
