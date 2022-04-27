package com.bilalfazlani

import org.bson.codecs.configuration.CodecProvider

trait MongoCodecProvider[T] {
  def get: CodecProvider
}

object MongoCodecProvider{
  def apply[T:MongoCodecProvider] = summon[MongoCodecProvider[T]]
}
