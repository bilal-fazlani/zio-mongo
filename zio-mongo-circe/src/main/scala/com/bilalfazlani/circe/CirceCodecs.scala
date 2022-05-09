package com.bilalfazlani.zioMongo.circe

import com.mongodb.MongoClientException
import io.circe.Decoder
import io.circe.Encoder
import io.circe.Json
import io.circe.JsonObject
import io.circe.parser.{ decode => circeDecode }
import org.bson.BsonReader
import org.bson.BsonType
import org.bson.BsonWriter
import org.bson.Document
import org.bson.codecs.Codec
import org.bson.codecs.DecoderContext
import org.bson.codecs.DocumentCodec
import org.bson.codecs.EncoderContext
import org.bson.codecs.StringCodec
import org.bson.codecs.configuration.CodecProvider
import org.bson.codecs.configuration.CodecRegistry
import org.bson.types.ObjectId

import java.time.Instant
import java.time.LocalDate
import scala.reflect.ClassTag
import scala.util.Try

case class MongoJsonParsingException(jsonString: String, message: String) extends MongoClientException(message)

given Encoder[ObjectId] =
  Encoder.encodeJsonObject.contramap[ObjectId](i => JsonObject("$oid" -> Json.fromString(i.toHexString)))
given Decoder[ObjectId] =
  Decoder.decodeJsonObject.emapTry(id => Try(ObjectId(id("$oid").flatMap(_.asString).get)))

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

given jCodec[T: Encoder: Decoder: ClassTag](using registry: CodecRegistry): Codec[T] = {
  given Class[T]        = summon[ClassTag[T]].runtimeClass.asInstanceOf[Class[T]]
  given Codec[Document] = new DocumentCodec(registry).asInstanceOf[Codec[Document]]
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