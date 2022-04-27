package com.bilalfazlani

import org.bson.codecs.configuration.CodecRegistries.fromProviders
import org.bson.codecs.configuration.{ CodecRegistry => JCodecRegistry }

object CodecRegistry {
  def apply[T: MongoCodecProvider]: JCodecRegistry = fromProviders(
    MongoCodecProvider[T].get
  )
  
}
