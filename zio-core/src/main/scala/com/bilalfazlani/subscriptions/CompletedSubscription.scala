package com.bilalfazlani.subscriptions

import com.bilalfazlani.result.Completed
import zio.IO
import org.reactivestreams.Subscriber

case class CompletedSubscription(p: JavaPublisher[Void]) extends Subscription[Completed] {

  override def fetch: IO[Throwable, Completed] = IO.async[Any, Throwable, Completed] { callback =>
    p.subscribe {
      new Subscriber[Void] {

        override def onSubscribe(s: org.reactivestreams.Subscription): Unit = s.request(1)

        override def onNext(t: Void): Unit = ()

        override def onError(t: Throwable): Unit = callback(IO.fail(t))

        override def onComplete(): Unit = callback(IO.succeed(Completed()))
      }
    }
  }
}
