package io.gatling.thrift

import com.twitter.util.Future
import io.gatling.thrift.action.ThriftActionBuilder
import io.gatling.thrift.protocol.{ThriftProtocol, ThriftProtocolBuilder}

trait ThriftDsl {

  implicit val thriftProtocol: ThriftProtocol

  def thrift: ThriftProtocolBuilder = ThriftProtocolBuilder()

  implicit def thriftProtocolBuilderToThriftProtocol(
      builder: ThriftProtocolBuilder
  ): ThriftProtocol = builder.build()

  implicit def callbackToThriftActionBuilder[A](
      callback: => Future[A]
  ): ThriftActionBuilder[A] = ThriftActionBuilder(callback)

}
