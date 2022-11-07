package com.bilalfazlani.zioMongo

type JCodec[T] = org.bson.codecs.Codec[T]

object JCodec {
  def apply[T: JCodec]: JCodec[T] = summon[JCodec[T]]
}
