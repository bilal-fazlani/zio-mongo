package com.bilalfazlani.subscriptions

import com.bilalfazlani.result.Completed
import zio.IO

case class CompletedSubscription(p: JavaPublisher[Void]) extends Subscription[Completed] {

  override def fetch: IO[Throwable, Completed] = IO.async[Any, Throwable, Completed] { callback =>
    p.subscribe {
      new JavaSubscriber[Void] {

        override def onSubscribe(s: JavaSubscription): Unit = s.request(1)

        override def onNext(t: Void): Unit = ()

        override def onError(t: Throwable): Unit = callback(IO.fail(t))

        override def onComplete(): Unit = callback(IO.succeed(Completed()))
      }
    }
  }
}
