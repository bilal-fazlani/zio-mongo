package com.bilalfazlani.zioMongo.example

import org.bson.types.ObjectId
import io.circe.Codec
import com.bilalfazlani.zioMongo.codecs.circe.given
import io.circe.generic.semiauto.deriveCodec

case class Person(_id: ObjectId, name: String, lastName: String, age: Int)

object Person:
  given Codec[Person] = deriveCodec
