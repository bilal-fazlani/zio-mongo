package com.bilalfazlani.subscriptions

import zio.IO
import zio.stream.ZStream

trait StreamSubscription[T] {
  def fetch: ZStream[Any, Throwable, T]
}

trait Subscription[T] {
  def fetch: IO[Throwable, T]
}
