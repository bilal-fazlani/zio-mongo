package com.bilalfazlani.subscriptions

import zio.IO

trait Subscription[T] {

  def fetch: IO[Throwable, T]

}
