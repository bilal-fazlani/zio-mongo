package example

import com.bilalfazlani.zioMongo.codecs.zioJson.{ given, * }
import zio.json.JsonCodec
import org.bson.types.ObjectId

case class Person(
    _id: ObjectId,
    name: String,
    lastName: String,
    age: Int
) derives JsonCodec
