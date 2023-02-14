package example2

import org.bson.types.ObjectId
import com.bilalfazlani.zioMongo.codecs.circe.given
import io.circe.generic.semiauto._
import io.circe.Codec

case class Person(
    _id: ObjectId,
    name: String,
    lastName: String,
    age: Int
)

object Person {
  given Codec[Person] = deriveCodec[Person]
}
