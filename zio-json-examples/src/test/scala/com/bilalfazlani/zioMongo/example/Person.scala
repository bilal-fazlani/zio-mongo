package com.bilalfazlani.zioMongo
package example

import codecs.zioJson.given
import org.bson.types.ObjectId
import zio.json.DeriveJsonCodec
import zio.json.JsonCodec

case class Person(_id: ObjectId, name: String, lastName: String, age: Int)

object Person {
  given JsonCodec[Person] = DeriveJsonCodec.gen
}