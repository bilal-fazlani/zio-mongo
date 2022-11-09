# Deriving ZIO-JSON codecs

You can derive codes for case classes using `derives` keyword

```scala
import codecs.zioJson.{ given, * }
import zio.json.JsonCodec
import org.bson.types.ObjectId

case class Person(
  _id: ObjectId, 
  name: String, 
  lastName: String, 
  age: Int
) derives JsonCodec
```

Another way to derive codecs is manualling invoking a macro

```scala
import codecs.zioJson.given
import org.bson.types.ObjectId
import zio.json.*

case class Person(
  _id: ObjectId, 
  name: String, 
  lastName: String, 
  age: Int
)

object Person {
  given JsonCodec[Person] = DeriveJsonCodec.gen
}
```

