package com.bilalfazlani.circe

import com.mongodb.MongoClientException
import io.circe.{ Decoder, Encoder, Json, JsonObject }
import io.circe.parser.{ decode => circeDecode }
import org.bson.types.ObjectId
import org.bson.codecs.configuration.{ CodecProvider, CodecRegistry }
import org.bson.codecs.{ Codec, DecoderContext, DocumentCodec, EncoderContext, StringCodec }
import org.bson.{ BsonReader, BsonType, BsonWriter, Document }

import java.time.{ Instant, LocalDate }
import scala.reflect.ClassTag
import scala.util.Try
import com.bilalfazlani.MongoCodecProvider

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

given circeCodecProvider[T: Encoder: Decoder: ClassTag]: MongoCodecProvider[T] =
  new MongoCodecProvider[T] {
    implicit val classT: Class[T]   = implicitly[ClassTag[T]].runtimeClass.asInstanceOf[Class[T]]
    override def get: CodecProvider = circeBasedCodecProvider[T]
  }

private def circeBasedCodecProvider[T](implicit enc: Encoder[T], dec: Decoder[T], classT: Class[T]): CodecProvider =
  new CodecProvider {
    override def get[Y](classY: Class[Y], registry: CodecRegistry): Codec[Y] =
      if (classY == classT || classT.isAssignableFrom(classY))
        new Codec[Y] {
          private val documentCodec: Codec[Document] = new DocumentCodec(registry).asInstanceOf[Codec[Document]]
          private val stringCodec: Codec[String]     = new StringCodec()
          override def encode(writer: BsonWriter, t: Y, encoderContext: EncoderContext): Unit = {
            val json = enc(t.asInstanceOf[T])
            if (json.isObject) {
              val document = Document.parse(json.noSpaces)
              documentCodec.encode(writer, document, encoderContext)
            } else {
              stringCodec.encode(writer, json.noSpaces.replaceAll("\"", ""), encoderContext)
            }
          }
          override def getEncoderClass: Class[Y] = classY
          override def decode(reader: BsonReader, decoderContext: DecoderContext): Y =
            reader.getCurrentBsonType match {
              case BsonType.DOCUMENT =>
                val json = documentCodec.decode(reader, decoderContext).toJson()
                circeDecode[T](json).fold(e => throw MongoJsonParsingException(json, e.getMessage), _.asInstanceOf[Y])
              case _ =>
                val string = stringCodec.decode(reader, decoderContext)
                dec
                  .decodeJson(Json.fromString(string))
                  .fold(e => throw MongoJsonParsingException(string, e.getMessage), _.asInstanceOf[Y])
            }
        }
      else null // scalastyle:ignore
  }
