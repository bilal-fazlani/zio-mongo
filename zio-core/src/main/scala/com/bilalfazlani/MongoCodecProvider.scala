package com.bilalfazlani

import org.bson.codecs.configuration.{ CodecProvider => JCodecProvider }
import org.bson.codecs.{ Codec => JCodec }

trait MongoCodecProvider[T] {
  def get: JCodecProvider
}

object MongoCodecProvider {
  def apply[T: MongoCodecProvider]: MongoCodecProvider[T] = summon[MongoCodecProvider[T]]
}

// trait MongoCodec[T] {
//   def get: JCodec[T]
// }

type JCodec[T] = org.bson.codecs.Codec[T]

object JCodec {
  def apply[T: JCodec]: JCodec[T] = summon[JCodec[T]]
}
