package com.bilalfazlani.subscriptions

import zio.{IO, Ref}

case class SingleItemSubscription[T](p: JavaPublisher[T]) extends Subscription[T] {

  override def fetch: IO[Throwable, T] = IO.async[Any, Throwable, T] { callback =>
    p.subscribe {
      new JavaSubscriber[T] {
        @volatile
        var item: T = _

        override def onSubscribe(s: JavaSubscription): Unit = s.request(1)

        override def onNext(t: T): Unit = item = t

        override def onError(t: Throwable): Unit = callback(IO.fail(t))

        override def onComplete(): Unit = callback(IO.succeed(item))
      }
    }
  }

  def headOption: IO[Throwable, Option[T]] = IO.async[Any, Throwable, Option[T]] { callback =>
    p.subscribe {
      new JavaSubscriber[T] {
        @volatile
        var item: T = _

        override def onSubscribe(s: JavaSubscription): Unit = s.request(1)

        override def onNext(t: T): Unit = item = t

        override def onError(t: Throwable): Unit = callback(IO.fail(t))

        override def onComplete(): Unit = callback(IO.succeed(Option(item)))
      }
    }
  }

}

