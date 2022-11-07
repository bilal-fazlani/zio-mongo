package com.bilalfazlani.zioMongo
package codecs.zioJson

import com.mongodb.MongoClientException
import org.bson.codecs.*
import org.bson.codecs.configuration.{CodecProvider, CodecRegistry}
import org.bson.json.JsonObject
import org.bson.types.ObjectId
import org.bson.{BsonReader, BsonType, BsonWriter, Document}
import org.mongodb.scala.MongoClient.DEFAULT_CODEC_REGISTRY
import zio.json.ast.{Json, JsonCursor}
import zio.json.{JsonDecoder, JsonEncoder}

import java.time.{Instant, LocalDate}
import scala.reflect.ClassTag
import scala.util.Try

case class MongoJsonParsingException(jsonString: String, message: String) extends MongoClientException(message)

given JsonEncoder[ObjectId] =
  JsonEncoder[Json.Obj].contramap[ObjectId](i => Json.Obj("$oid" -> Json.Str(i.toHexString)))
given JsonDecoder[ObjectId] = JsonDecoder[Json.Obj].map(o =>
  o.get(JsonCursor.field("$oid").isString)
    .fold(e => throw MongoJsonParsingException(o.toString, e), v => ObjectId(v.value))
)

given JsonEncoder[Instant] = JsonEncoder[Json.Obj].contramap[Instant](i => Json.Obj("$date" -> Json.Str(i.toString)))
given JsonDecoder[Instant] = JsonDecoder[Json.Obj].map(dateObj =>
  dateObj
    .get(JsonCursor.field("$date").isString)
    .fold(e => throw MongoJsonParsingException(dateObj.toString, e), v => Instant.parse(v.value))
)

given JsonEncoder[LocalDate] =
  JsonEncoder[Json.Obj].contramap[LocalDate](i => Json.Obj("$date" -> Json.Str(i.toString)))
given JsonDecoder[LocalDate] = JsonDecoder[Json.Obj].map(dateObj =>
  dateObj
    .get(JsonCursor.field("$date").isString)
    .fold(e => throw MongoJsonParsingException(dateObj.toString, e), v => LocalDate.parse(v.value))
)

given jCodec[T: JsonEncoder: JsonDecoder: ClassTag]: Codec[T] = {
  given Class[T]        = summon[ClassTag[T]].runtimeClass.asInstanceOf[Class[T]]
  given Codec[Document] = new DocumentCodec(DEFAULT_CODEC_REGISTRY).asInstanceOf[Codec[Document]]
  generateJCodec[T]
}

private def generateJCodec[T](using
    enc: JsonEncoder[T],
    dec: JsonDecoder[T],
    classT: Class[T],
    documentCodec: Codec[Document]
): Codec[T] =
  new Codec[T] {
    private val stringCodec: Codec[String] = new StringCodec()
    override def encode(writer: BsonWriter, t: T, encoderContext: EncoderContext): Unit = {
      val json: Json = enc.toJsonAST(t).toOption.get
      if (json.isInstanceOf[Json.Obj]) {
        val document = Document.parse(json.toString())
        documentCodec.encode(writer, document, encoderContext)
      } else {
        stringCodec.encode(writer, json.toString().replaceAll("\"", ""), encoderContext)
      }
    }
    override def getEncoderClass: Class[T] = classT
    override def decode(reader: BsonReader, decoderContext: DecoderContext): T =
      reader.getCurrentBsonType match {
        case BsonType.DOCUMENT =>
          val json = documentCodec.decode(reader, decoderContext).toJson()
          dec
            .decodeJson(json)
            .fold(
              e => throw MongoJsonParsingException(json, e),
              identity
            )
        case _ =>
          val string = stringCodec.decode(reader, decoderContext)
          dec
            .decodeJson(string)
            .fold(e => throw MongoJsonParsingException(string, e), identity)
      }
  }
