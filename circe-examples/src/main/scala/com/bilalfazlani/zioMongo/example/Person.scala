package example

import org.bson.types.ObjectId
import com.bilalfazlani.zioMongo.codecs.circe.given
import io.circe.Codec.AsObject

case class Person(
    _id: ObjectId,
    name: String,
    lastName: String,
    age: Int
) derives AsObject
