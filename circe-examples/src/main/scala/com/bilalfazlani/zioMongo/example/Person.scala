package com.bilalfazlani.zioMongo.example

import org.bson.types.ObjectId

case class Person(_id: ObjectId, name: String, lastName: String, age: Int)