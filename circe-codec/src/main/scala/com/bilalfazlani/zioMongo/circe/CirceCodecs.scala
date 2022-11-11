package com.bilalfazlani.zioMongo
package codecs.circe

import com.mongodb.MongoClientException
import io.circe.{Codec => CirceCodec, *}
import io.circe.parser.{ decode => circeDecode }
import io.circe.generic.semiauto.deriveCodec
import org.bson.*
import org.bson.codecs.{Encoder => BsonEncoder, Decoder => BsonDecoder, *}
import org.bson.codecs.configuration.CodecProvider
import org.bson.codecs.configuration.CodecRegistry
import org.mongodb.scala.MongoClient.DEFAULT_CODEC_REGISTRY
import org.bson.types.ObjectId

import scala.deriving.Mirror
import java.time.Instant
import java.time.LocalDate
import scala.reflect.ClassTag
import scala.util.Try


extension (t: CirceCodec.type) inline def derived[T: Mirror.Of]: CirceCodec[T] = deriveCodec[T]

case class MongoJsonParsingException(jsonString: String, message: String) extends MongoClientException(message)

given Encoder[ObjectId] =
  Encoder.encodeJsonObject.contramap[ObjectId](i => JsonObject("$oid" -> Json.fromString(i.toHexString)))
given Decoder[ObjectId] =
  Decoder.decodeJsonObject.emapTry(o => Try(ObjectId(o("$oid").flatMap(_.asString).get)))

given Encoder[Instant] =
  Encoder.encodeJsonObject.contramap[Instant](i => JsonObject("$date" -> Json.fromString(i.toString)))
given Decoder[Instant] =
  Decoder.decodeJsonObject.emapTry(dateObj => Try(Instant.parse(dateObj("$date").flatMap(_.asString).get)))

given Encoder[LocalDate] =
  Encoder.encodeJsonObject.contramap[LocalDate](i => JsonObject("$date" -> Json.fromString(i.toString)))
given Decoder[LocalDate] =
  Decoder.decodeJsonObject.emapTry(dateObj =>
    Try(LocalDate.parse(dateObj("$date").flatMap(_.asString).map(_.slice(0, 10)).get))
  )

given jCodec[T: Encoder: Decoder: ClassTag]: Codec[T] = {
  given Class[T]        = summon[ClassTag[T]].runtimeClass.asInstanceOf[Class[T]]
  given Codec[Document] = new DocumentCodec(DEFAULT_CODEC_REGISTRY).asInstanceOf[Codec[Document]]
  generateJCodec[T]
}

private def generateJCodec[T](using
    enc: Encoder[T],
    dec: Decoder[T],
    classT: Class[T],
    documentCodec: Codec[Document]
): Codec[T] =
  new Codec[T] {
    private val stringCodec: Codec[String] = new StringCodec()
    override def encode(writer: BsonWriter, t: T, encoderContext: EncoderContext): Unit = {
      val json = enc(t)
      if (json.isObject) {
        val document = Document.parse(json.noSpaces)
        documentCodec.encode(writer, document, encoderContext)
      } else {
        stringCodec.encode(writer, json.noSpaces.replaceAll("\"", ""), encoderContext)
      }
    }
    override def getEncoderClass: Class[T] = classT
    override def decode(reader: BsonReader, decoderContext: DecoderContext): T =
      reader.getCurrentBsonType match {
        case BsonType.DOCUMENT =>
          val json = documentCodec.decode(reader, decoderContext).toJson()
          circeDecode[T](json).fold(e => throw MongoJsonParsingException(json, e.getMessage), identity)
        case _ =>
          val string = stringCodec.decode(reader, decoderContext)
          dec
            .decodeJson(Json.fromString(string))
            .fold(e => throw MongoJsonParsingException(string, e.getMessage), identity)
      }
  }
