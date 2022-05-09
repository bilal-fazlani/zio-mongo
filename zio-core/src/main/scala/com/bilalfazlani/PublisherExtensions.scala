package com.bilalfazlani

import org.reactivestreams.Publisher
import zio.{ZIO, Task}
import zio.interop.reactivestreams.*
import zio.stream.ZSink
import com.bilalfazlani.result.Completed

extension [T](publisher: Publisher[T]) {
  def toZIO = for {
    maybe  <- publisher.toZIOStream(2).runHead
    result <- ZIO.fromOption(maybe)
                .mapError(_ => new NoSuchElementException("no element in the stream"))
  } yield result

  def toCompleted: Task[Completed] = toZIO.map(_ => Completed())
}