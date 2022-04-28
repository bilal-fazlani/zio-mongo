package com.bilalfazlani

import scala.reflect.ClassTag

package object subscriptions {

  // type JavaSubscription = org.reactivestreams.Subscription

  type JavaPublisher[T] = org.reactivestreams.Publisher[T]

  def clazz[T](ct: ClassTag[T]): Class[T] =  ct.runtimeClass.asInstanceOf[Class[T]]

}
