package com.bilalfazlani.subscriptions
import zio.interop.reactivestreams.*

import zio.{ IO, Ref }
import zio.stream.ZSink
import zio.ZIO
import java.{ util => ju }

case class SingleItemSubscription[T](p: JavaPublisher[T]) extends Subscription[T] {

  override def fetch = for {
    maybe  <- headOption
    result <- ZIO.fromOption(maybe).mapError(_ => new ju.NoSuchElementException("no element in the stream"))
  } yield result

  def headOption: IO[Throwable, Option[T]] = p.toStream(1).run(ZSink.head)

}
