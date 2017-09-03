package io.gatling.thrift

import com.twitter.util.Future
import io.gatling.thrift.action.ThriftActionBuilder
import io.gatling.thrift.data.Connection

trait ThriftDsl {

  implicit val connection: Connection

  implicit val callback: Future[_]

  implicit def callbackToThrifActionBuilder[A](
    callback: => Future[A]
  ): ThriftActionBuilder[A] =
    ThriftActionBuilder(callback)

}
