# Codec derivation

To perform any database operation which involves data fetching or updating, it is required to serialize and deserialize data to and from BSON. zio-mongo provides codecs for all the basic types and also provides a way to derive codecs for your own types. To encode and decode case classes and sealed traits, zio-mongo can be used with zio-json or circe.

[Deriving ZIO-Json codecs](zio-json.md)

[Deriving Circe codecs](circe.md)