package com

import scala.reflect.ClassTag


package object bilalfazlani {

  type JavaMongoDatabase = com.mongodb.reactivestreams.client.MongoDatabase

  type JavaMongoCollection[T] = com.mongodb.reactivestreams.client.MongoCollection[T]

  type JavaMongoClient = com.mongodb.reactivestreams.client.MongoClient

  def clazz[T](ct: ClassTag[T]) =  ct.runtimeClass.asInstanceOf[Class[T]]

}
