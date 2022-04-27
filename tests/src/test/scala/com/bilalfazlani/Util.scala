package com.bilalfazlani

import zio.*

def randomString(n: Int) =
  val list  = (('a' to 'z') ++ ('A' to 'Z') ++ (0 to 9)).map(_.toString).toList
  Random.shuffle(list)
    .map(_.take(10).mkString)
    .map(str => s"collection-$str")
    .debug("***** collection name")

  // val index = Random.nextIntBetween(0, list.length)
  // ZIO
  //   .collectAll(List.fill(n)(index.map(list(_))))
  //   .map(_.mkString)
  //   .map(str => s"collection-$str")
  //   .debug("***** collection name")

object TestApp extends zio.ZIOAppDefault {
  override def run =
    ZIO
      .collectAll(List.fill(5)(randomString(10)))
      .flatMap(names => ZIO.collectAll(names.map(n => Console.printLine(n))))
}
